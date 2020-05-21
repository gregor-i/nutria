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
import nutria.frontend.util.LenseUtils
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
    viewport: Viewport = Viewport.mandelbrot, // todo: remove
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
      template = Examples.timeEscape,
      viewport = Viewport.mandelbrot
    )
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
          parameters(TemplateEditorState.parameters)
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

  def parameters(lens: Lens[State, Vector[Parameter]])(implicit state: State, update: NutriaState => Unit): Seq[Node] = {
    val createNewParameter = Node("section")
      .child(Node("h5.title.is-5").text("Add Parameter"))
      .child(
        Form.selectInput[TemplateEditorState, Option[Parameter]](
          label = "parameter type",
          options = Seq(
            "Type"    -> None,
            "Integer" -> Some(IntParameter("parameter_name", 0)),
            "Float"   -> Some(FloatParameter("parameter_name", 0.0)),
            "Color"   -> Some(RGBAParameter("parameter_name", RGB.white.withAlpha())),
            "Function1 f: (lambda) => C" -> Some(
              InitialFunctionParameter("function_name", StringFunction.unsafe("lambda"))
            ),
            "Function1 f: (lambda) => C, with derivative: (lambda) => C" -> Some(
              InitialFunctionParameter("function_name", StringFunction.unsafe("lambda"), includeDerivative = true)
            ),
            "Function2 f: (z, lambda) => C" -> Some(FunctionParameter("function_name", StringFunction.unsafe("z + lambda"))),
            "Function2 f: (z, lambda) => C, with derivative: (z, lambda) => C" -> Some(
              NewtonFunctionParameter("function_name", StringFunction.unsafe("z + lambda"), includeDerivative = true)
            ),
            "Function2 f: (z, lambda) => C, with derivative: (z, z', lambda) => C" -> Some(
              FunctionParameter("function_name", StringFunction.unsafe("z + lambda"), includeDerivative = true)
            )
          ),
          lens = TemplateEditorState.newParameter,
          eqFunction = (left, right) => {
            (left, right) match {
              case (None, None)                                                           => true
              case (Some(l: InitialFunctionParameter), Some(r: InitialFunctionParameter)) => r.includeDerivative == l.includeDerivative
              case (Some(l: NewtonFunctionParameter), Some(r: NewtonFunctionParameter))   => r.includeDerivative == l.includeDerivative
              case (Some(l: FunctionParameter), Some(r: FunctionParameter))               => r.includeDerivative == l.includeDerivative
              case (Some(l), Some(r))                                                     => l.getClass == r.getClass
              case _                                                                      => false
            }
          }
        )
      )
      .childOptional(
        state.newParameter.map(
          _ =>
            Form.stringInput(
              "parameter name",
              TemplateEditorState.newParameter
                .composePrism(monocle.std.all.some[Parameter])
                .composeLens(Parameter.name)
                .pipe(LenseUtils.unsafeOptional)
            )
        )
      )
      .childOptional(
        state.newParameter.map(
          _ =>
            ParameterForm.apply {
              TemplateEditorState.newParameter
                .composePrism(monocle.std.all.some[Parameter])
                .pipe(LenseUtils.unsafeOptional)
            }
        )
      )
      .childOptional(
        state.newParameter.map(p => Node("pre").text(FragmentShaderSource.parameter(p)))
      )
      .childOptional(
        state.newParameter.map(
          p =>
            Button
              .list()
              .child(
                Button(
                  "Add Parameter",
                  Icons.plus,
                  Snabbdom.event(
                    _ =>
                      update(
                        state.copy(
                          newParameter = None,
                          template = state.template.copy(parameters = state.template.parameters :+ p)
                        )
                      )
                  )
                ).classes("is-primary", "is-outline")
              )
        )
      )

    ParameterForm.list(lens) ++ Seq(createNewParameter)
  }

  def preview()(implicit state: State, update: NutriaState => Unit) = {
    val tile =
      Node("article.fractal-tile.is-relative")
        .child(
          FractalTile(FractalImage(state.template, state.viewport, nutria.core.refineUnsafe(1)), Dimensions.thumbnail)
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
    val buttons: Seq[Node] = state.user match {
      case Some(user) =>
        Seq(buttonSave)
      case None =>
        Seq.empty
    }

    Node("div.field.is-grouped.is-grouped-right")
      .child(buttons.map(button => Node("p.control").child(button)))
  }

  private def buttonSave(implicit state: State, update: NutriaState => Unit) =
    Button(
      "Save Changes as new Template",
      Icons.save,
      Actions.saveTemplate(
        Entity(
          title = "todo",
          value = state.template
        )
      )
    ).classes("is-primary")
}
