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
    remoteFractal: WithId[FractalImageEntity],
    fractalToEdit: WithId[FractalImageEntity]
) extends PageState {
  def dirty: Boolean = remoteFractal != fractalToEdit
}

object DetailsState extends LenseUtils {
  val fractalToEdit_entity       = fractalToEdit.composeLens(WithId.entity)
  val fractalToEdit_entity_image = fractalToEdit_entity.composeLens(Entity.value)
}

object DetailsPage extends Page[DetailsState] {
  override def stateFromUrl = {
    case (user, s"/fractals/${fractalId}/details", _) =>
      Links.detailsState(fractalId).loading()
  }

  override def stateToUrl(state: State): Option[(Path, QueryParameter)] =
    Some(s"/fractals/${state.remoteFractal.id}/details" -> Map.empty)

  def render(implicit context: Context) =
    Body()
      .child(common.Header())
      .child(
        Header
          .fab(Node("button"))
          .pipe {
            case node if context.local.dirty && User.isOwner(context.global.user, context.local.remoteFractal) =>
              node
                .child(Icons.icon(Icons.save))
                .event("click", Actions.updateFractal(context.local.fractalToEdit))
            case node if !User.isOwner(context.global.user, context.local.remoteFractal) =>
              node
                .child(Icons.icon(Icons.copy))
                .event("click", Actions.saveAsNewFractal(context.local.fractalToEdit.entity.copy(published = false)))
            case node if !context.local.dirty =>
              node
                .child(Icons.icon(Icons.save))
                .attr("disabled", "disabled")
          }
      )
      .child(body)
      .child(common.Footer())

  def body(implicit context: Context) =
    Node("div.container")
      .child(
        Node("section.section")
          .child(Node("h1.title.is-1").text(Option(context.local.remoteFractal.entity.title).filter(_.nonEmpty).getOrElse("<No Title given>")))
          .child(Node("h2.subtitle").text(context.local.remoteFractal.entity.description))
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

  def parameters(lens: Lens[State, Vector[Parameter]])(implicit context: Context) = {
    ParameterForm.list(lens) :+ Form.forLens(
      label = "Anti Aliase (multi sampling)",
      description = "Define the Anti Aliase factor. It should be something like 1, 2 ... 4",
      lens = DetailsState.fractalToEdit_entity_image.composeLens(FractalImage.antiAliase)
    )
  }

  def preview()(implicit context: Context) =
    Node("div.fractal-tile-list")
      .child(
        InteractiveFractal
          .forImage(DetailsState.fractalToEdit_entity_image)
          .classes("fractal-tile")
          .style("maxHeight", "100vh")
          .style("minHeight", "50vh")
      )

  private def actions()(implicit context: Context): Node = {
    val buttons: Seq[Node] = () match {
      case _ if User.isOwner(context.global.user, context.local.remoteFractal) =>
        Seq(buttonDelete, buttonFork, buttonUpdate, buttonExplore)

      case _ =>
        Seq(buttonFork, buttonExplore)
    }

    ButtonList(buttons: _*)
  }

  private def buttonUpdate(implicit context: Context) =
    Button("Update", Icons.save, Actions.updateFractal(context.local.fractalToEdit))
      .classes("is-primary")

  private def buttonDelete(implicit context: Context) =
    Button("Delete", Icons.delete, Actions.deleteFractalFromDetails(context.local.fractalToEdit.id))
      .classes("is-danger", "is-light")

  private def buttonFork(implicit context: Context) =
    Button("Clone", Icons.copy, Actions.saveAsNewFractal(context.local.fractalToEdit.entity.copy(published = false)))
      .classes("is-primary")

  private def buttonExplore(implicit context: Context) =
    Link(ExplorerState(remoteFractal = Some(context.local.remoteFractal), fractalImage = context.local.fractalToEdit.entity))
      .classes("button")
      .child(Icons.icon(Icons.explore))
      .child(Node("span").text("Explore"))
}
