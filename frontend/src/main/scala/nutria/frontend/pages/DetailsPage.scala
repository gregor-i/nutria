package nutria.frontend.pages

import monocle.Lens
import monocle.macros.Lenses
import nutria.api.{Entity, FractalImageEntity, User, WithId}
import nutria.core._
import nutria.frontend.Router.{Path, QueryParameter}
import nutria.frontend._
import nutria.frontend.pages.common._
import nutria.frontend.util.LenseUtils
import snabbdom.Node

import scala.util.chaining._

@Lenses
case class DetailsState(
    user: Option[User],
    remoteFractal: WithId[FractalImageEntity],
    fractalToEdit: WithId[FractalImageEntity],
    navbarExpanded: Boolean = false
) extends NutriaState {
  def dirty: Boolean                                            = remoteFractal != fractalToEdit
  override def setNavbarExtended(boolean: Boolean): NutriaState = copy(navbarExpanded = boolean)
}

object DetailsState extends LenseUtils {
  val fractalToEdit_entity       = fractalToEdit.composeLens(WithId.entity)
  val fractalToEdit_entity_image = fractalToEdit_entity.composeLens(Entity.value)
}

object DetailsPage extends Page[DetailsState] {
  override def stateFromUrl = {
    case (user, s"/fractals/${fractalId}/details", _) =>
      Links.detailsState(user, fractalId).loading(user)
  }

  override def stateToUrl(state: State): Option[(Path, QueryParameter)] =
    Some(s"/fractals/${state.remoteFractal.id}/details" -> Map.empty)

  def render(implicit state: State, update: NutriaState => Unit) =
    Body()
      .child(common.Header())
      .child(
        Header
          .fab(Node("button"))
          .pipe {
            case node if state.dirty && User.isOwner(state.user, state.remoteFractal) =>
              node
                .child(Icons.icon(Icons.save))
                .event("click", Actions.updateFractal(state.fractalToEdit))
            case node if !User.isOwner(state.user, state.remoteFractal) =>
              node
                .child(Icons.icon(Icons.copy))
                .event("click", Actions.saveAsNewFractal(state.fractalToEdit.entity))
            case node if !state.dirty =>
              node
                .child(Icons.icon(Icons.save))
                .attr("disabled", "disabled")
          }
      )
      .child(body(state, update))
      .child(common.Footer())

  def body(implicit state: State, update: NutriaState => Unit) =
    Node("div.container")
      .child(
        Node("section.section")
          .child(Node("h1.title.is-1").text(Option(state.remoteFractal.entity.title).filter(_.nonEmpty).getOrElse("<No Title given>")))
          .child(Node("h2.subtitle").text(state.remoteFractal.entity.description))
      )
      .child(EntityAttributes.section(DetailsState.fractalToEdit_entity))
      .child(
        Node("section.section").children(
          Node("h4.title.is-4").text("Parameters:"),
          parameters(DetailsState.fractalToEdit_entity_image.composeLens(FractalImage.appliedParameters))
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

  def parameters(lens: Lens[State, Vector[Parameter]])(implicit state: State, update: NutriaState => Unit) = {
    ParameterForm.list(lens)
  }

  def preview()(implicit state: State, update: NutriaState => Unit) =
    Node("div.fractal-tile-list")
      .child(
        InteractiveFractal
          .forImage(DetailsState.fractalToEdit_entity_image)
          .classes("fractal-tile")
          .style("maxHeight", "100vh")
          .style("minHeight", "50vh")
      )

  private def actions()(implicit state: State, update: NutriaState => Unit): Node = {
    val buttons: Seq[Node] = state.user match {
      case Some(user) if user.id == state.remoteFractal.owner =>
        Seq(buttonDelete, buttonFork, buttonUpdate, buttonExplore)

      case _ =>
        Seq(buttonFork, buttonExplore)
    }

    ButtonList(buttons: _*)
  }

  private def buttonUpdate(implicit state: State, update: NutriaState => Unit) =
    Button("Update", Icons.save, Actions.updateFractal(state.fractalToEdit))
      .classes("is-primary")

  private def buttonDelete(implicit state: State, update: NutriaState => Unit) =
    Button("Delete", Icons.delete, Actions.deleteFractal(state.fractalToEdit.id))
      .classes("is-danger", "is-light")

  private def buttonFork(implicit state: State, update: NutriaState => Unit) =
    Button("Clone", Icons.copy, Actions.saveAsNewFractal(state.fractalToEdit.entity))
      .classes("is-primary")

  private def buttonExplore(implicit state: State, update: NutriaState => Unit) =
    Link(ExplorerState(user = state.user, remoteFractal = Some(state.remoteFractal), fractalImage = state.fractalToEdit.entity))
      .classes("button")
      .child(Icons.icon(Icons.explore))
      .child(Node("span").text("Explore"))
}
