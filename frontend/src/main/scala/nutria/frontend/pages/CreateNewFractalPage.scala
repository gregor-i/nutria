package nutria.frontend.pages

import io.circe.Codec
import monocle.macros.{GenPrism, Lenses}
import monocle.{Lens, Prism}
import nutria.CirceCodec
import nutria.api.{Entity, FractalImageEntity, FractalTemplateEntity, User}
import nutria.core._
import nutria.frontend.Router.Location
import nutria.frontend._
import nutria.frontend.pages.CreateNewFractalState.{ParametersStep, TemplateStep}
import nutria.frontend.pages.common._
import nutria.frontend.service.TemplateService
import nutria.frontend.util.{LenseUtils, SnabbdomUtil, Updatable}
import snabbdom.Node

import scala.concurrent.Future
import scala.util.chaining._

@Lenses
case class CreateNewFractalState(
    templates: Vector[FractalTemplateEntity],
    step: CreateNewFractalState.Step = CreateNewFractalState.TemplateStep,
    navbarExpanded: Boolean = false
) extends PageState

object CreateNewFractalState extends ExecutionContext {
  sealed trait Step
  case object TemplateStep extends Step
  @Lenses
  case class ParametersStep(template: FractalTemplateEntity) extends Step

  object Step extends CirceCodec {
    implicit val codec: Codec[Step] = semiauto.deriveConfiguredCodec
  }

  def load(globalState: GlobalState): Future[CreateNewFractalState] =
    for {
      publicTemplates <- TemplateService.listPublic()
      userTemplates <- globalState.user match {
        case Some(user) => TemplateService.listUser(user.id)
        case None       => Future.successful(Vector.empty)
      }
      allTemplates = (publicTemplates.toVector ++ userTemplates).distinctBy(_.id).map(_.entity)
    } yield CreateNewFractalState(templates = allTemplates)

  val formulaStep: Prism[Step, ParametersStep] =
    GenPrism[Step, ParametersStep]
}

object CreateNewFractalPage extends Page[CreateNewFractalState] {
  override def stateFromUrl = {
    case (globalState, "/new-fractal", queryParameter) =>
      val stepFromUrl = queryParameter
        .get("step")
        .flatMap(Router.queryDecoded[CreateNewFractalState.Step])

      CreateNewFractalState
        .load(globalState)
        .map(state => stepFromUrl.fold(state)(step => state.copy(step = step)))
        .loading()
  }

  override def stateToUrl(state: CreateNewFractalState): Option[Location] =
    Some(("/new-fractal", Map("step" -> Router.queryEncoded(state.step))))

  override def render(implicit globalState: GlobalState, updatable: Updatable[State, PageState]): Node =
    Body()
      .child(Header(CreateNewFractalState.navbarExpanded))
      .child(
        Node("div.container")
          .child(Node("section.section").child(Node("h1.title.is-1").text("Create new Fractal")))
          .child {
            state.step match {
              case TemplateStep =>
                selectTemplate()
              case ParametersStep(_) =>
                modifyParameters(
                  CreateNewFractalState.step
                    .composePrism(CreateNewFractalState.formulaStep)
                    .composeLens(ParametersStep.template)
                    .pipe(LenseUtils.unsafeOptional)
                )
            }
          }
      )
      .child(Footer())

  private def selectTemplate()(implicit globalState: GlobalState, updatable: Updatable[State, PageState]): Node =
    Node("section.section")
      .child(Node("h4.title.is-4").text("Step 1: Select the fractal template"))
      .child(
        Node("div.fractal-tile-list")
          .child(
            state.templates.map { template =>
              Link(state.copy(step = ParametersStep(template)))
                .classes("fractal-tile")
                .attr("title", template.title)
                .child(FractalTile(FractalImage.fromTemplate(template.value), Dimensions.thumbnail))
            }
          )
          .child(GalleryPage.dummyTiles)
      )

  private def modifyParameters(
      templateLens: Lens[State, FractalTemplateEntity]
  )(implicit globalState: GlobalState, updatable: Updatable[State, PageState]): Node = {
    val image = templateLens.get(state).map(FractalImage.fromTemplate)

    Node("section.section")
      .child(Node("h4.title.is-4").text("Step 2: Modify parameters"))
      .child(ParameterForm.list(templateLens.composeLens(Entity.value).composeLens(FractalTemplate.parameters)))
      .child(
        Node("div.fractal-tile-list")
          .child(
            InteractiveFractal
              .forTemplate(templateLens.composeLens(Entity.value))
              .classes("fractal-tile")
              .style("maxHeight", "100vh")
              .style("minHeight", "50vh")
          )
      )
      .child(
        ButtonList(
          backButton(),
          finishButton(image)
        )
      )
  }

  private def backButton()(implicit globalState: GlobalState, updatable: Updatable[State, PageState]): Node =
    Button("Choose a different Template", Icons.cancel, SnabbdomUtil.updateT[State](_.copy(step = TemplateStep)))

  private def finishButton(fractal: FractalImageEntity)(implicit globalState: GlobalState, updatable: Updatable[State, PageState]): Node =
    Button("Save Fractal", Icons.save, Actions.saveAsNewFractal(fractal))
      .classes("is-primary")
}
