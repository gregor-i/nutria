package nutria.frontend.pages

import mathParser.complex.Complex
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
import org.scalajs.dom.HTMLTextAreaElement
import snabbdom.components.{Button, ButtonList, Modal}
import snabbdom.{Event, Node}

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
    case (_, s"/templates/${templateId}/editor", queryParams) =>
      (for {
        remoteTemplate <- TemplateService.get(templateId)
      } yield TemplateEditorState(
        remoteTemplate = Some(remoteTemplate),
        entity = queryParams.get("state").flatMap(Router.queryDecoded[FractalTemplateEntity]).getOrElse(remoteTemplate.entity)
      )).loading()

    case (_, s"/templates/editor", queryParams) =>
      val templateFromUrl =
        queryParams
          .get("state")
          .flatMap(Router.queryDecoded[FractalTemplateEntity])
          .getOrElse(Entity(value = FractalTemplate.empty))

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

  def render(implicit context: Context) =
    Body()
      .child(common.Header())
      .child(body)
      .child(common.Footer())

  def body(implicit context: Context) =
    "div.container"
      .child(
        "section.section"
          .child("h1.title.is-1".text("Fractal Template"))
//          .child("h2.subtitle".text("description")
      )
      .child(EntityAttributes.section(TemplateEditorState.entity))
      .child(
        "section.section".children(
          "h4.title.is-4".text("Template:"),
          template(TemplateEditorState.template)
        )
      )
      .child(
        "section.section".children(
          "h4.title.is-4".text("Parameters:"),
          parameters(),
          openModalButton()
        )
      )
      .child(
        "section.section".children(
          "h4.title.is-4".text("Preview:"),
          preview()
        )
      )
      .child(
        "section.section"
          .child(actions())
      )
      .child(parameterModal(TemplateEditorState.newParameter, TemplateEditorState.parameters))

  def template(
      lens: Lens[State, FractalTemplate]
  )(implicit context: Context) = {
    val codeEditor =
      "div.code-editor-container"
        .child(
          "pre.code-editor-line-numbers".text {
            (1 to (context.local.entity.value.code.count(_ == '\n') + 1))
              .map(number => s"${number}:")
              .mkString("\n")
          }
        )
        .child(
          "textarea.code-editor.is-family-code"
            .event[Event](
              "input",
              Debounce(
                event => {
                  event.target
                    .asInstanceOf[HTMLTextAreaElement]
                    .value
                    .pipe(lens.composeLens(FractalTemplate.code).set(_)(context.local))
                    .tap(context.update)
                },
                250
              )
            )
            .text(context.local.entity.value.code)
        )

    Seq(
      codeEditor,
      CompileStatus(context.local.entity.value)
    )
  }

  def parameterModal(
      lensToMaybeParameter: Lens[State, Option[Parameter]],
      lensToOtherParameters: Lens[State, Vector[Parameter]]
  )(implicit context: Context): Option[Node] =
    lensToMaybeParameter.get(context.local).map { parameter =>
      val lensToParameter = lensToMaybeParameter.composePrism(monocle.std.option.some).pipe(LenseUtils.unsafeOptional)
      val overwrite       = lensToOtherParameters.get(context.local).exists(_.name == parameter.name)

      val selectType = Form.selectInput[State, Parameter](
        label = "parameter type",
        options = Seq(
          "Integer"        -> IntParameter("parameter_name", value = 0),
          "Float"          -> FloatParameter("parameter_name", value = 0.0),
          "Complex"        -> ComplexParameter("parameter_name", value = Complex(0.0, 0.0)),
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

      Modal(closeAction = Some(SnabbdomUtil.modify(lensToMaybeParameter.set(None))))(
        "h5.title.is-5".text("Add Parameter"),
        selectType,
        Form.forLens("name", description = "", lens = lensToParameter.composeLens(Parameter.name)),
        Form.forLens("description", description = "", lens = lensToParameter.composeLens(Parameter.description)),
        Label(
          label = "generated code:",
          node = "pre"
            .text(FragmentShaderSource.parameter(lensToParameter.get(context.local)))
            .style("whiteSpace", "break-spaces"),
          actions = Seq.empty
        ),
        ButtonList.right(
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

  def parameters()(implicit context: Context): Seq[Node] = {
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

  def openModalButton()(implicit context: Context) =
    ButtonList.right(
      Button(
        "Add new Parameter",
        Icons.plus,
        SnabbdomUtil.modify(TemplateEditorState.newParameter.set(Some(IntParameter("parameter_name", value = 0))))
      ).classes("is-marginless")
    )

  def preview()(implicit context: Context) =
    "div.fractal-tile-list"
      .child(
        InteractiveFractal
          .forTemplate(TemplateEditorState.template)
          .classes("fractal-tile")
          .style("maxHeight", "100vh")
          .style("minHeight", "50vh")
      )

  private def actions()(implicit context: Context): Node = {
    val buttons: Seq[Node] = (context.global.user, context.local.remoteTemplate) match {
      case (Some(user), Some(remote)) if user.id == remote.owner =>
        Seq(buttonDelete, buttonSave, buttonUpdate)
      case (Some(_), _) =>
        Seq(buttonSave)
      case (None, _) =>
        Seq.empty
    }

    ButtonList.right(buttons: _*)
  }

  private def buttonSave(implicit context: Context) =
    Button(
      "Save as new Template",
      Icons.save,
      Actions.saveTemplate(context.local.entity.copy(published = false))
    ).classes("is-primary")
      .boolAttr("disabled", !context.local.dirty)

  private def buttonUpdate(implicit context: Context) =
    Button(
      "Update existing Template",
      Icons.save,
      Actions.updateTemplate(context.local.remoteTemplate.get.copy(entity = context.local.entity))
    ).classes("is-primary")
      .boolAttr("disabled", !context.local.dirty)

  private def buttonDelete(implicit context: Context) =
    Button(
      "Delete",
      Icons.delete,
      Actions.deleteTemplate(context.local.remoteTemplate.get.id)
    ).classes("is-danger", "is-light")
}
