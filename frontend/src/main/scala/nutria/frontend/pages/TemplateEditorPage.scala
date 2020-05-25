package nutria.frontend.pages

import monocle.{Iso, Lens, Optional}
import monocle.function.{At, Index}
import monocle.macros.Lenses
import nutria.api.{Entity, FractalTemplateEntityWithId, User, WithId}
import nutria.core._
import nutria.core.languages.{Lambda, StringFunction, ZAndLambda}
import nutria.frontend.Router.{Path, QueryParameter}
import nutria.frontend._
import nutria.frontend.pages.common.{Form, _}
import nutria.frontend.service.NutriaService
import nutria.frontend.util.{LenseUtils, SnabbdomUtil}
import nutria.shaderBuilder.{CompileException, FractalRenderer, FragmentShaderSource}
import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.{HTMLInputElement, HTMLTextAreaElement, WebGLRenderingContext}
import snabbdom.{Node, Snabbdom}

import scala.scalajs.js
import scala.util.{Failure, Success, Try}
import scala.util.chaining._

@Lenses
case class TemplateEditorState(
    user: Option[User],
    remoteTemplate: Option[FractalTemplateEntityWithId],
    template: FractalTemplate,
    newParameter: Option[Parameter] = None,
    navbarExpanded: Boolean = false
) extends NutriaState {
  def dirty: Boolean                                   = remoteTemplate.fold(FractalTemplate.empty)(_.entity.value) != template
  def setNavbarExtended(boolean: Boolean): NutriaState = copy(navbarExpanded = boolean)
}

object TemplateEditorState extends LenseUtils {
  val code       = template.composeLens(FractalTemplate.code)
  val parameters = template.composeLens(FractalTemplate.parameters)

  def initial(implicit nutriaState: NutriaState): TemplateEditorState =
    TemplateEditorState(
      user = nutriaState.user,
      remoteTemplate = None,
      template = Examples.timeEscape
    )

  def byTemplate(template: FractalTemplateEntityWithId)(implicit nutriaState: NutriaState): TemplateEditorState =
    TemplateEditorState(user = nutriaState.user, remoteTemplate = Some(template), template = template.entity.value)
}

object TemplateEditorPage extends Page[TemplateEditorState] {

  override def stateFromUrl: PartialFunction[(Path, QueryParameter), NutriaState] = {
    case (s"/templates/${templateId}/editor", queryParams) =>
      LoadingState(
        for {
          user           <- NutriaService.whoAmI()
          remoteTemplate <- NutriaService.loadTemplate(templateId)
        } yield TemplateEditorState(
          user = user,
          remoteTemplate = Some(remoteTemplate),
          template = queryParams.get("state").flatMap(Router.queryDecoded[FractalTemplate]).getOrElse(remoteTemplate.entity.value)
        )
      )

    case (s"/templates/editor", queryParams) =>
      val templateFromUrl =
        queryParams.get("state").flatMap(Router.queryDecoded[FractalTemplate]).getOrElse(FractalTemplate.empty)

      LoadingState(
        for {
          user <- NutriaService.whoAmI()
        } yield TemplateEditorState(
          user = user,
          remoteTemplate = None,
          template = templateFromUrl
        )
      )
  }

  override def stateToUrl(state: State): Option[(Path, QueryParameter)] = {
    val query: QueryParameter = if (state.dirty) Map("state" -> Router.queryEncoded(state.template)) else Map.empty
    state.remoteTemplate match {
      case Some(remoteTemplate) =>
        Some(s"/templates/${remoteTemplate.id}/editor" -> query)
      case None =>
        Some(s"/templates/editor" -> query)
    }
  }

  def render(implicit state: State, update: NutriaState => Unit) =
    Body()
      .child(common.Header())
      .child(body(state, update))
      .child(common.Footer())

  def body(implicit state: State, update: NutriaState => Unit) =
    Node("div.container")
      .child(
        Node("section.section")
          .child(Node("h1.title.is-1").text("Fractal Template"))
//          .child(Node("h2.subtitle").text("description")
      )
      .child(
        Node("section.section").children(
          Node("h4.title.is-4").text("Template:"),
          template(TemplateEditorState.template)
        )
      )
      .child(
        Node("section.section").children(
          Node("h4.title.is-4").text("Parameters:"),
          parameters()
        )
      )
      .child(
        Node("section.section").children(
          Node("h4.title.is-4").text("Preview:"),
          preview()
        )
      )
      //      .child(
      //        Node("section.section").children(
      //          Node("h4.title.is-4").text("Constructed Fragment Shader:"),
      //          source()
      //        )
      //      )
      .child(
        Node("section.section")
          .child(actions())
      )
      .child(parameterModal(TemplateEditorState.newParameter, TemplateEditorState.parameters))

  def template(lens: Lens[State, FractalTemplate])(implicit state: State, update: NutriaState => Unit) = {
    val codeEditor =
      Node("div.code-editor-container")
        .child(
          Node("pre.code-editor-line-numbers").text {
            (1 to (state.template.code.count(_ == '\n') + 1))
              .map(number => s"${number}:")
              .mkString("\n")
          }
        )
        .child(
          Node("textarea.code-editor.is-family-code")
            .event(
              "input",
              Snabbdom.event { event =>
                event.target
                  .asInstanceOf[HTMLTextAreaElement]
                  .value
                  .pipe(lens.composeLens(FractalTemplate.code).set(_)(state))
                  .tap(update)
              }
            )
            .text(state.template.code)
        )

    Seq(
      codeEditor,
      CompileStatus(state.template)
    )
  }

  def parameterModal(
      lensToMaybeParameter: Lens[State, Option[Parameter]],
      lensToOtherParameters: Lens[State, Vector[Parameter]]
  )(implicit state: State, update: NutriaState => Unit): Option[Node] =
    lensToMaybeParameter.get(state).map { parameter =>
      val lensToParameter = lensToMaybeParameter.composePrism(monocle.std.option.some).pipe(LenseUtils.unsafeOptional)
      val overwrite       = lensToOtherParameters.get(state).exists(_.name == parameter.name)

      val selectType = Form.selectInput[State, Parameter](
        label = "parameter type",
        options = Seq(
          "Integer" -> IntParameter("parameter_name", 0),
          "Float"   -> FloatParameter("parameter_name", 0.0),
          "Color"   -> RGBAParameter("parameter_name", RGB.white.withAlpha()),
          "Function1 f: (lambda) => C" ->
            InitialFunctionParameter("function_name", StringFunction.unsafe("lambda")),
          "Function1 f: (lambda) => C, with derivative: (lambda) => C" ->
            InitialFunctionParameter("function_name", StringFunction.unsafe("lambda"), includeDerivative = true),
          "Function2 f: (z, lambda) => C" -> FunctionParameter("function_name", StringFunction.unsafe("z + lambda")),
          "Function2 f: (z, lambda) => C, with derivative: (z, lambda) => C" ->
            NewtonFunctionParameter("function_name", StringFunction.unsafe("z + lambda"), includeDerivative = true),
          "Function2 f: (z, lambda) => C, with derivative: (z, z', lambda) => C" ->
            FunctionParameter("function_name", StringFunction.unsafe("z + lambda"), includeDerivative = true)
        ),
        lens = lensToParameter,
        eqFunction = (left, right) => {
          (left, right) match {
            case (l: InitialFunctionParameter, r: InitialFunctionParameter) => r.includeDerivative == l.includeDerivative
            case (l: NewtonFunctionParameter, r: NewtonFunctionParameter)   => r.includeDerivative == l.includeDerivative
            case (l: FunctionParameter, r: FunctionParameter)               => r.includeDerivative == l.includeDerivative
            case (l, r)                                                     => l.getClass == r.getClass
          }
        }
      )

      Modal(closeAction = SnabbdomUtil.update(lensToMaybeParameter.set(None)))(
        Node("h5.title.is-5").text("Add Parameter"),
        selectType,
        Form.forLens("name", lensToParameter.composeLens(Parameter.name)),
        ParameterForm(lensToParameter),
        Label(
          label = "generated code:",
          node = Node("pre")
            .text(FragmentShaderSource.parameter(lensToParameter.get(state)))
            .style("whiteSpace", "break-spaces"),
          actions = Seq.empty
        ),
        ButtonList(
          Button("Cancel", Icons.cancel, SnabbdomUtil.update(lensToMaybeParameter.set(None))),
          Button(
            if (overwrite) "Overwrite" else "Add",
            Icons.plus,
            SnabbdomUtil.update(
              lensToOtherParameters
                .modify(list => Parameter.setParameter(list, parameter))
                .andThen(lensToMaybeParameter.set(None))
            )
          ).classes("is-primary")
        )
      )
    }

  def parameters()(implicit state: State, update: NutriaState => Unit): Seq[Node] = {
    val lens = TemplateEditorState.parameters
    val openModalButton =
      ButtonList(
        Button("Add new Parameter", Icons.plus, SnabbdomUtil.update(TemplateEditorState.newParameter.set(Some(IntParameter("parameter_name", 0)))))
          .classes("is-marginless")
      )

    ParameterForm.list(lens) ++ Seq(openModalButton)
  }

  def preview()(implicit state: State, update: NutriaState => Unit) = {
    val tile =
      Node("article.fractal-tile")
        .child(
          FractalTile(FractalImage(state.template, state.template.exampleViewport, nutria.core.refineUnsafe(1)), Dimensions.thumbnail)
        )

    Node("div.fractal-tile-list")
      .child(tile)
  }

  def source()(implicit state: State) =
    Node("pre").text(
      FragmentShaderSource(state.template, nutria.core.refineUnsafe(1)).linesIterator.zipWithIndex
        .map { case (line, number) => s"${number + 1}: $line" }
        .mkString("\n")
    )

  private def actions()(implicit state: State, update: NutriaState => Unit): Node = {
    val buttons: Seq[Node] = (state.user, state.remoteTemplate) match {
      case (Some(user), Some(remote)) if user.id == remote.owner =>
        Seq(buttonSave, buttonUpdate)
      case (Some(_), _) =>
        Seq(buttonSave)
      case (None, _) =>
        Seq.empty
    }

    ButtonList(buttons: _*)
  }

  private def buttonSave(implicit state: State, update: NutriaState => Unit) =
    Button(
      "Save as new Template",
      Icons.save,
      Actions.saveTemplate(
        Entity(
          title = "todo",
          value = state.template
        )
      )
    ).classes("is-primary")
      .boolAttr("disabled", !state.dirty)

  private def buttonUpdate(implicit state: State, update: NutriaState => Unit) =
    Button(
      "Update existing Template",
      Icons.save,
      Actions.saveTemplate(
        Entity(
          title = "todo",
          value = state.template
        )
      )
    ).classes("is-primary")
      .boolAttr("disabled", !state.dirty)
}
