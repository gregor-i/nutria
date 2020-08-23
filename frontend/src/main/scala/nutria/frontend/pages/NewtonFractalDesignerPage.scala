package nutria.frontend.pages

import mathParser.complex.Complex
import monocle.Lens
import monocle.macros.{GenLens, Lenses}
import nutria.api.User
import nutria.core._
import nutria.frontend.Router.{Path, QueryParameter}
import nutria.frontend.pages.common.{AnimatedFractalTile, Body, Form, Header}
import nutria.frontend.util.LenseUtils
import nutria.frontend.{NutriaState, Page}
import nutria.shaderBuilder.FragmentShaderSource
import org.scalajs.dom.html
import org.scalajs.dom.raw.DragEvent
import snabbdom.{Node, Snabbdom}

@Lenses
case class NewtonFractalDesignerState(
    user: Option[User],
    constant: Complex = Complex(1, 0),
    viewport: Viewport = Viewport.aroundZero.cover(Dimensions.preview.width, Dimensions.preview.height),
    numberOfRoots: Int = 3,
    seed: Int = 123123,
    navbarExpanded: Boolean = false
) extends NutriaState

object NewtonFractalDesignerState {
  def real: Lens[Complex, Double] = GenLens[Complex](_.real)
  def imag: Lens[Complex, Double] = GenLens[Complex](_.imag)
}

object NewtonFractalDesignePage extends Page[NewtonFractalDesignerState] {
  override def stateFromUrl: PartialFunction[(Option[User], Path, QueryParameter), NutriaState] = {
    case (user, "/newton-fractal-designer", _) => NewtonFractalDesignerState(user)
  }

  override def stateToUrl(state: State): Option[(Path, QueryParameter)] =
    Some("/newton-fractal-designer" -> Map.empty)

  override def render(implicit state: State, update: NutriaState => Unit): Node =
    Body()
      .child(Header(NewtonFractalDesignerState.navbarExpanded))
      .child(
        Node("div.container")
          .child(fractalTile())
          .child(inputs())
      )

  private def fractalTile()(implicit state: State, update: NutriaState => Unit) =
    Node("div.fractal-tile-list")
      .child {
        Node("div.fractal-tile")
          .style("height", Dimensions.preview.height + "px")
          .style("width", Dimensions.preview.width + "px")
          .child(AnimatedFractalTile(fractalImageOverTime(state)))
      }

  private def fractalImageOverTime(state: State): LazyList[FractalImage] =
    NewtonFractalDesigner
      .animation(
        constant = state.constant,
        roots = Seq.tabulate(state.numberOfRoots)(i => Complex(Math.sin(i) * 0.5, Math.cos(i) * 0.5)),
        seed = state.seed
      )

  private def inputs()(implicit state: State, update: NutriaState => Unit) =
    Node("div.section")
      .child(Form.forLens("constant real", lens = NewtonFractalDesignerState.constant.composeLens(NewtonFractalDesignerState.real)))
      .child(Form.forLens("constant imag", lens = NewtonFractalDesignerState.constant.composeLens(NewtonFractalDesignerState.imag)))
      .child(Form.forLens("number of roots", lens = NewtonFractalDesignerState.numberOfRoots))
      .child(Form.forLens("seed", lens = NewtonFractalDesignerState.seed))
}
