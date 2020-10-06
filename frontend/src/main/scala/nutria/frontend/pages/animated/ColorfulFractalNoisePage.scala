package nutria.frontend
package pages
package animated

import monocle.macros.Lenses
import nutria.core._
import nutria.frontend.Router.{Path, QueryParameter}
import nutria.frontend.pages.common.{AnimatedFractalTile, Body, Form, Header}
import snabbdom.Node

@Lenses
case class ColorfulFractalNoiseState(
    t0: Double = 0.0,
    deltaT: Double = 0.1
) extends PageState

object ColorfulFractalNoisePage extends Page[ColorfulFractalNoiseState] {
  override def stateFromUrl: PartialFunction[(GlobalState, Path, QueryParameter), PageState] = {
    case (_, "/animated/fractal-noise", params) =>
      ColorfulFractalNoiseState(
        t0 = params.get("t0").flatMap(_.toDoubleOption).getOrElse(0.0),
        deltaT = params.get("deltaT").flatMap(_.toDoubleOption).getOrElse(0.1)
      )
  }

  override def stateToUrl(state: State): Option[(Path, QueryParameter)] =
    Some(
      "/animated/fractal-noise" -> Map(
        "t0"     -> state.t0.toString,
        "deltaT" -> state.deltaT.toString
      )
    )

  def render(implicit context: Context): Node =
    Body()
      .child(Header())
      .child(
        Node("div.container")
          .child(fractalTile())
          .child(inputs())
      )

  private def fractalTile()(implicit context: Context) =
    Node("div.fractal-tile-list")
      .child {
        Node("div.fractal-tile")
          .style("height", Dimensions.preview.height.toString + "px")
          .style("width", Dimensions.preview.width.toString + "px")
          .child(AnimatedFractalTile(fractalImageOverTime(context.local)))
      }

  private def fractalImageOverTime(state: State): LazyList[FractalImage] = {
    val image = FractalImage.fromTemplate(Examples.colorfulFractalNoise)
    LazyList
      .iterate(state.t0)(_ + state.deltaT)
      .map(time => image.setParameters(Vector(FloatParameter("time", value = time))))
  }

  private def inputs()(implicit context: Context) =
    Node("div.section")
      .child(
        Form.forLens(
          "t0",
          description = "at what point in time should the animation start. Default: 0",
          lens = ColorfulFractalNoiseState.t0
        )
      )
      .child(
        Form.forLens(
          "deltaT",
          description = "how fast should the animation be. Default: 0.1",
          lens = ColorfulFractalNoiseState.deltaT
        )
      )
}
