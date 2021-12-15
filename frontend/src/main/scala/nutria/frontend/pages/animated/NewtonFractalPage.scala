package nutria.frontend
package pages
package animated

import mathParser.complex.Complex
import monocle.Lens
import monocle.macros.{GenLens, Lenses}
import nutria.core._
import nutria.frontend.Router.{Path, QueryParameter}
import nutria.frontend.pages.common.{AnimatedFractalTile, Body, Form, Header}
import snabbdom.Node

@Lenses
case class NewtonFractalState(
    constant: Complex = Complex(1, 0),
    viewport: Viewport = Viewport.aroundZero.cover(Dimensions.preview.width, Dimensions.preview.height),
    numberOfRoots: Int = 3,
    seed: Int,
    alpha: Double,
    beta: Double,
    gamma: Double
) extends PageState

object NewtonFractalState {
  def real: Lens[Complex, Double] = GenLens[Complex](_.real)
  def imag: Lens[Complex, Double] = GenLens[Complex](_.imag)
}

object NewtonFractalPage extends Page[NewtonFractalState] {
  override def stateFromUrl: PartialFunction[(GlobalState, Path, QueryParameter), PageState] = { case (_, "/animated/newton-fractal", params) =>
    NewtonFractalState(
      alpha = params.get("alpha").flatMap(_.toDoubleOption).getOrElse(0.05),
      beta = params.get("beta").flatMap(_.toDoubleOption).getOrElse(0.01),
      gamma = params.get("gamma").flatMap(_.toDoubleOption).getOrElse(0.995),
      seed = params.get("seed").flatMap(_.toIntOption).getOrElse(123123),
      numberOfRoots = params.get("numberOfRoots").flatMap(_.toIntOption).getOrElse(5)
    )
  }

  override def stateToUrl(state: State): Option[(Path, QueryParameter)] =
    Some(
      "/animated/newton-fractal" -> Map(
        "alpha"         -> state.alpha.toString,
        "beta"          -> state.beta.toString,
        "gamma"         -> state.gamma.toString,
        "seed"          -> state.seed.toString,
        "numberOfRoots" -> state.numberOfRoots.toString
      )
    )

  def render(implicit context: Context): Node =
    Body()
      .child(Header())
      .child(
        "div.container"
          .child(fractalTile())
          .child(inputs())
      )

  private def fractalTile()(implicit context: Context) =
    "div.fractal-tile-list"
      .child {
        "div.fractal-tile"
          .style("height", Dimensions.preview.height.toString + "px")
          .style("width", Dimensions.preview.width.toString + "px")
          .child(AnimatedFractalTile(fractalImageOverTime(context.local)))
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

  private def inputs()(implicit context: Context) =
    "div.section"
      .child(
        Form.forLens(
          "constant real",
          description = "real part of the integration constant",
          lens = NewtonFractalState.constant.composeLens(NewtonFractalState.real)
        )
      )
      .child(
        Form.forLens(
          "constant imag",
          description = "imaginary part of the integration constant",
          lens = NewtonFractalState.constant.composeLens(NewtonFractalState.imag)
        )
      )
      .child(Form.forLens("number of roots", description = "how many roots should be generated", lens = NewtonFractalState.numberOfRoots))
      .child(
        Form.forLens(
          "alpha",
          description = "parameter for random walk: how fast does the root change direction",
          lens = NewtonFractalState.alpha
        )
      )
      .child(
        Form.forLens("beta", description = "parameter for random walk: how fast does the root move foreward", lens = NewtonFractalState.beta)
      )
      .child(
        Form.forLens(
          "gamma",
          description = "parameter for random walk: how much are the roots bound to origin",
          lens = NewtonFractalState.gamma
        )
      )
      .child(
        Form
          .forLens("seed", description = "seed for the random number generation, needed for moving the roots", lens = NewtonFractalState.seed)
      )
}
