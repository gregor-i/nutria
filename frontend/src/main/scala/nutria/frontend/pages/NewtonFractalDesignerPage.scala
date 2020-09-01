package nutria.frontend.pages

import mathParser.complex.Complex
import monocle.Lens
import monocle.macros.{GenLens, Lenses}
import nutria.api.User
import nutria.core._
import nutria.frontend.Router.{Path, QueryParameter}
import nutria.frontend.pages.common.{AnimatedFractalTile, Body, Form, Header}
import nutria.frontend.{NutriaState, Page}
import snabbdom.Node

@Lenses
case class NewtonFractalDesignerState(
    user: Option[User],
    constant: Complex = Complex(1, 0),
    viewport: Viewport = Viewport.aroundZero.cover(Dimensions.preview.width, Dimensions.preview.height),
    numberOfRoots: Int = 3,
    seed: Int,
    alpha: Double,
    beta: Double,
    gamma: Double,
    navbarExpanded: Boolean = false
) extends NutriaState

object NewtonFractalDesignerState {
  def real: Lens[Complex, Double] = GenLens[Complex](_.real)
  def imag: Lens[Complex, Double] = GenLens[Complex](_.imag)
}

object NewtonFractalDesignePage extends Page[NewtonFractalDesignerState] {
  override def stateFromUrl: PartialFunction[(Option[User], Path, QueryParameter), NutriaState] = {
    case (user, "/newton-fractal-designer", params) =>
      NewtonFractalDesignerState(
        user = user,
        alpha = params.get("alpha").flatMap(_.toDoubleOption).getOrElse(0.05),
        beta = params.get("beta").flatMap(_.toDoubleOption).getOrElse(0.01),
        gamma = params.get("gamma").flatMap(_.toDoubleOption).getOrElse(0.995),
        seed = params.get("seed").flatMap(_.toIntOption).getOrElse(123123),
        numberOfRoots = params.get("numberOfRoots").flatMap(_.toIntOption).getOrElse(5)
      )
  }

  override def stateToUrl(state: State): Option[(Path, QueryParameter)] =
    Some(
      "/newton-fractal-designer" -> Map(
        "alpha"         -> state.alpha.toString,
        "beta"          -> state.beta.toString,
        "gamma"         -> state.gamma.toString,
        "seed"          -> state.seed.toString,
        "numberOfRoots" -> state.numberOfRoots.toString
      )
    )

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
        seed = state.seed,
        alpha = state.alpha,
        beta = state.beta,
        gamma = state.gamma
      )

  private def inputs()(implicit state: State, update: NutriaState => Unit) =
    Node("div.section")
      .child(
        Form.forLens(
          "constant real",
          description = "real part of the integration constant",
          lens = NewtonFractalDesignerState.constant.composeLens(NewtonFractalDesignerState.real)
        )
      )
      .child(
        Form.forLens(
          "constant imag",
          description = "imaginary part of the integration constant",
          lens = NewtonFractalDesignerState.constant.composeLens(NewtonFractalDesignerState.imag)
        )
      )
      .child(Form.forLens("number of roots", description = "how many roots should be generated", lens = NewtonFractalDesignerState.numberOfRoots))
      .child(
        Form.forLens(
          "alpha",
          description = "parameter for random walk: how fast does the root change direction",
          lens = NewtonFractalDesignerState.alpha
        )
      )
      .child(
        Form.forLens("beta", description = "parameter for random walk: how fast does the root move foreward", lens = NewtonFractalDesignerState.beta)
      )
      .child(
        Form.forLens(
          "gamma",
          description = "parameter for random walk: how much are the roots bound to origin",
          lens = NewtonFractalDesignerState.gamma
        )
      )
      .child(
        Form
          .forLens("seed", description = "seed for the random number generation, needed for moving the roots", lens = NewtonFractalDesignerState.seed)
      )
}
