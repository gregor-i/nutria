package nutria.frontend.pages

import monocle.Lens
import monocle.macros.Lenses
import nutria.api.{Entity, FractalTemplateEntity, FractalTemplateEntityWithId, User}
import nutria.core._
import nutria.core.languages.StringFunction
import nutria.frontend.Router.{Path, QueryParameter}
import nutria.frontend._
import nutria.frontend.facades.Debounce
import nutria.frontend.pages.common.{Form, _}
import nutria.frontend.service.TemplateService
import nutria.frontend.util.{LenseUtils, SnabbdomUtil}
import nutria.shaderBuilder.FragmentShaderSource
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLTextAreaElement
import snabbdom.{Node, Snabbdom}

import scala.scalajs.js
import scala.util.chaining._

@Lenses
case class TemplateEditorState(
    user: Option[User],
    remoteTemplate: Option[FractalTemplateEntityWithId],
    entity: FractalTemplateEntity,
    newParameter: Option[Parameter] = None,
    navbarExpanded: Boolean = false
) extends NutriaState {
  def dirty: Boolean = remoteTemplate.fold(true)(_.entity != entity)
}

object TemplateEditorState extends LenseUtils {
  val template   = entity.composeLens(Entity.value)
  val code       = template.composeLens(FractalTemplate.code)
  val parameters = template.composeLens(FractalTemplate.parameters)

  def initial(implicit nutriaState: NutriaState): TemplateEditorState =
    TemplateEditorState(
      user = nutriaState.user,
      remoteTemplate = None,
      entity = Entity(value = Examples.timeEscape)
    )

  def byTemplate(template: FractalTemplateEntityWithId)(implicit nutriaState: NutriaState): TemplateEditorState =
    TemplateEditorState(user = nutriaState.user, remoteTemplate = Some(template), entity = template.entity)
}

object TemplateEditorPage extends Page[TemplateEditorState] {

  override def stateFromUrl = {
    case (user, s"/templates/${templateId}/editor", queryParams) =>
      (for {
        remoteTemplate <- TemplateService.get(templateId)
      } yield TemplateEditorState(
        user = user,
        remoteTemplate = Some(remoteTemplate),
        entity = queryParams.get("state").flatMap(Router.queryDecoded[FractalTemplateEntity]).getOrElse(remoteTemplate.entity)
      )).loading(user)

    case (user, s"/templates/editor", queryParams) =>
      val templateFromUrl =
        queryParams.get("state").flatMap(Router.queryDecoded[FractalTemplateEntity]).getOrElse(Entity(value = FractalTemplate.empty))

      TemplateEditorState(
        user = user,
        remoteTemplate = None,
        entity = templateFromUrl
      )
  }

  override def stateToUrl(state: State): Option[(Path, QueryParameter)] = {
    val query: QueryParameter = if (state.dirty) Map("state" -> Router.queryEncoded(state.entity)) else Map.empty
    state.remoteTemplate match {
      case Some(remoteTemplate) =>
        Some(s"/templates/${remoteTemplate.id}/editor" -> query)
      case None =>
        Some(s"/templates/editor" -> query)
    }
  }

  def render(implicit state: State, update: NutriaState => Unit) =
    Body()
      .child(common.Header(TemplateEditorState.navbarExpanded))
      .child(body(state, update))
      .child(common.Footer())

  def body(implicit state: State, update: NutriaState => Unit) =
    Node("div.container")
      .child(
        Node("section.section")
          .child(Node("h1.title.is-1").text("Fractal Template"))
//          .child(Node("h2.subtitle").text("description")
      )
      .child(EntityAttributes.section(TemplateEditorState.entity))
      .child(
        Node("section.section").children(
          Node("h4.title.is-4").text("Template:"),
          template(TemplateEditorState.template)
        )
      )
      .child(
        Node("section.section").children(
          Node("h4.title.is-4").text("Parameters:"),
          parameters(),
          openModalButton()
        )
      )
      .child(
        Node("section.section").children(
          Node("h4.title.is-4").text("Preview:"),
          preview()
        )
      )
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
            (1 to (state.entity.value.code.count(_ == '\n') + 1))
              .map(number => s"${number}:")
              .mkString("\n")
          }
        )
        .child(
          Node("textarea.code-editor.is-family-code")
            .event(
              "input",
              Debounce(
                Snabbdom.event { event =>
                  event.target
                    .asInstanceOf[HTMLTextAreaElement]
                    .value
                    .pipe(lens.composeLens(FractalTemplate.code).set(_)(state))
                    .tap(update)
                },
                250
              )
            )
            .text(state.entity.value.code)
        )

    Seq(
      codeEditor,
      CompileStatus(state.entity.value)
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
          "Integer"        -> IntParameter("parameter_name", value = 0),
          "Float"          -> FloatParameter("parameter_name", value = 0.0),
          "Color"          -> RGBAParameter("parameter_name", value = RGB.white.withAlpha()),
          "Color Gradient" -> ColorGradientParameter("parameter_name", value = Seq(RGB.white.withAlpha(), RGB.black.withAlpha())),
          "Function1 f: (lambda) => C" ->
            InitialFunctionParameter("function_name", value = StringFunction.unsafe("lambda")),
          "Function1 f: (lambda) => C, with derivative: (lambda) => C" ->
            InitialFunctionParameter("function_name", value = StringFunction.unsafe("lambda"), includeDerivative = true),
          "Function2 f: (z, lambda) => C" -> FunctionParameter("function_name", value = StringFunction.unsafe("z + lambda")),
          "Function2 f: (z, lambda) => C, with derivative: (z, lambda) => C" ->
            NewtonFunctionParameter("function_name", value = StringFunction.unsafe("z + lambda"), includeDerivative = true),
          "Function2 f: (z, lambda) => C, with derivative: (z, z', lambda) => C" ->
            FunctionParameter("function_name", value = StringFunction.unsafe("z + lambda"), includeDerivative = true)
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
        Form.forLens("name", lens = lensToParameter.composeLens(Parameter.name)),
        Form.forLens("description", lens = lensToParameter.composeLens(Parameter.description)),
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
    val actions: Parameter => Seq[(String, State => State)] = parameter =>
      Seq(
        Icons.edit   -> TemplateEditorState.newParameter.set(Some(parameter)),
        Icons.delete -> TemplateEditorState.parameters.modify(_.filter(_ != parameter))
      )

    ParameterForm.list(
      TemplateEditorState.parameters,
      actions
    )
  }

  def openModalButton()(implicit state: State, update: State => Unit) =
    ButtonList(
      Button(
        "Add new Parameter",
        Icons.plus,
        SnabbdomUtil.update(TemplateEditorState.newParameter.set(Some(IntParameter("parameter_name", value = 0))))
      ).classes("is-marginless")
    )

  def preview()(implicit state: State, update: NutriaState => Unit) =
    Node("div.fractal-tile-list")
      .child(
        InteractiveFractal
          .forTemplate(TemplateEditorState.template)
          .classes("fractal-tile")
          .style("maxHeight", "100vh")
          .style("minHeight", "50vh")
      )

  private def actions()(implicit state: State, update: NutriaState => Unit): Node = {
    val buttons: Seq[Node] = (state.user, state.remoteTemplate) match {
      case (Some(user), Some(remote)) if user.id == remote.owner =>
        Seq(buttonDelete, buttonSave, buttonUpdate)
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
      Actions.saveTemplate(state.entity.copy(published = false))
    ).classes("is-primary")
      .boolAttr("disabled", !state.dirty)

  private def buttonUpdate(implicit state: State, update: NutriaState => Unit) =
    Button(
      "Update existing Template",
      Icons.save,
      Actions.updateTemplate(state.remoteTemplate.get.copy(entity = state.entity))
    ).classes("is-primary")
      .boolAttr("disabled", !state.dirty)

  private def buttonDelete(implicit state: State, update: NutriaState => Unit) =
    Button(
      "Delete",
      Icons.delete,
      Actions.deleteTemplate(state.remoteTemplate.get.id)
    ).classes("is-danger", "is-light")
}
