package nutria.frontend.pages

import monocle.Lens
import monocle.macros.Lenses
import nutria.api.{Entity, FractalTemplateEntity, FractalTemplateEntityWithId}
import nutria.core._
import nutria.core.languages.StringFunction
import nutria.frontend.Router.{Path, QueryParameter}
import nutria.frontend._
import nutria.frontend.facades.Debounce
import nutria.frontend.pages.common.{Form, _}
import nutria.frontend.service.TemplateService
import nutria.frontend.util.{LenseUtils, SnabbdomUtil}
import nutria.shaderBuilder.FragmentShaderSource
import org.scalajs.dom.raw.HTMLTextAreaElement
import snabbdom.{Node, Snabbdom}

import scala.util.chaining._

@Lenses
case class TemplateEditorState(
    remoteTemplate: Option[FractalTemplateEntityWithId],
    entity: FractalTemplateEntity,
    newParameter: Option[Parameter] = None
) extends PageState {
  def dirty: Boolean = remoteTemplate.fold(true)(_.entity != entity)
}

object TemplateEditorState extends LenseUtils {
  val template   = entity.composeLens(Entity.value)
  val code       = template.composeLens(FractalTemplate.code)
  val parameters = template.composeLens(FractalTemplate.parameters)

  val initial: TemplateEditorState =
    TemplateEditorState(
      remoteTemplate = None,
      entity = Entity(value = Examples.timeEscape)
    )

  def byTemplate(template: FractalTemplateEntityWithId): TemplateEditorState =
    TemplateEditorState(remoteTemplate = Some(template), entity = template.entity)
}

object TemplateEditorPage extends Page[TemplateEditorState] {

  override def stateFromUrl = {
    case (user, s"/templates/${templateId}/editor", queryParams) =>
      (for {
        remoteTemplate <- TemplateService.get(templateId)
      } yield TemplateEditorState(
        remoteTemplate = Some(remoteTemplate),
        entity = queryParams.get("state").flatMap(Router.queryDecoded[FractalTemplateEntity]).getOrElse(remoteTemplate.entity)
      )).loading()

    case (user, s"/templates/editor", queryParams) =>
      val templateFromUrl =
        queryParams.get("state").flatMap(Router.queryDecoded[FractalTemplateEntity]).getOrElse(Entity(value = FractalTemplate.empty))

      TemplateEditorState(
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

  override def render(implicit global: Global, local: Local) =
    Body()
      .child(common.Header())
      .child(body)
      .child(common.Footer())

  def body(implicit global: Global, local: Local) =
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

  def template(
      lens: Lens[State, FractalTemplate]
  )(implicit global: Global, local: Local) = {
    val codeEditor =
      Node("div.code-editor-container")
        .child(
          Node("pre.code-editor-line-numbers").text {
            (1 to (local.state.entity.value.code.count(_ == '\n') + 1))
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
                    .pipe(lens.composeLens(FractalTemplate.code).set(_)(local.state))
                    .tap(local.update)
                },
                250
              )
            )
            .text(local.state.entity.value.code)
        )

    Seq(
      codeEditor,
      CompileStatus(local.state.entity.value)
    )
  }

  def parameterModal(
      lensToMaybeParameter: Lens[State, Option[Parameter]],
      lensToOtherParameters: Lens[State, Vector[Parameter]]
  )(implicit global: Global, local: Local): Option[Node] =
    lensToMaybeParameter.get(local.state).map { parameter =>
      val lensToParameter = lensToMaybeParameter.composePrism(monocle.std.option.some).pipe(LenseUtils.unsafeOptional)
      val overwrite       = lensToOtherParameters.get(local.state).exists(_.name == parameter.name)

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

      Modal(closeAction = SnabbdomUtil.modify(lensToMaybeParameter.set(None)))(
        Node("h5.title.is-5").text("Add Parameter"),
        selectType,
        Form.forLens("name", description = "", lens = lensToParameter.composeLens(Parameter.name)),
        Form.forLens("description", description = "", lens = lensToParameter.composeLens(Parameter.description)),
        Label(
          label = "generated code:",
          node = Node("pre")
            .text(FragmentShaderSource.parameter(lensToParameter.get(local.state)))
            .style("whiteSpace", "break-spaces"),
          actions = Seq.empty
        ),
        ButtonList(
          Button("Cancel", Icons.cancel, SnabbdomUtil.modify(lensToMaybeParameter.set(None))),
          Button(
            if (overwrite) "Overwrite" else "Add",
            Icons.plus,
            SnabbdomUtil.modify(
              lensToOtherParameters
                .modify(list => Parameter.setParameter(list, parameter))
                .andThen(lensToMaybeParameter.set(None))
            )
          ).classes("is-primary")
        )
      )
    }

  def parameters()(implicit global: Global, local: Local): Seq[Node] = {
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

  def openModalButton()(implicit global: Global, local: Local) =
    ButtonList(
      Button(
        "Add new Parameter",
        Icons.plus,
        SnabbdomUtil.modify(TemplateEditorState.newParameter.set(Some(IntParameter("parameter_name", value = 0))))
      ).classes("is-marginless")
    )

  def preview()(implicit global: Global, local: Local) =
    Node("div.fractal-tile-list")
      .child(
        InteractiveFractal
          .forTemplate(TemplateEditorState.template)
          .classes("fractal-tile")
          .style("maxHeight", "100vh")
          .style("minHeight", "50vh")
      )

  private def actions()(implicit global: Global, local: Local): Node = {
    val buttons: Seq[Node] = (global.state.user, local.state.remoteTemplate) match {
      case (Some(user), Some(remote)) if user.id == remote.owner =>
        Seq(buttonDelete, buttonSave, buttonUpdate)
      case (Some(_), _) =>
        Seq(buttonSave)
      case (None, _) =>
        Seq.empty
    }

    ButtonList(buttons: _*)
  }

  private def buttonSave(implicit global: Global, local: Local) =
    Button(
      "Save as new Template",
      Icons.save,
      Actions.saveTemplate(local.state.entity.copy(published = false))
    ).classes("is-primary")
      .boolAttr("disabled", !local.state.dirty)

  private def buttonUpdate(implicit global: Global, local: Local) =
    Button(
      "Update existing Template",
      Icons.save,
      Actions.updateTemplate(local.state.remoteTemplate.get.copy(entity = local.state.entity))
    ).classes("is-primary")
      .boolAttr("disabled", !local.state.dirty)

  private def buttonDelete(implicit global: Global, local: Local) =
    Button(
      "Delete",
      Icons.delete,
      Actions.deleteTemplate(local.state.remoteTemplate.get.id)
    ).classes("is-danger", "is-light")
}
