package nutria.core

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Interval.Open
import eu.timepit.refined.numeric.NonNaN
import eu.timepit.refined.refineMV
import io.circe.Codec
import monocle.Prism
import monocle.macros.GenPrism
import shapeless.Witness

sealed trait DivergingSeriesColoring

@monocle.macros.Lenses()
case class TimeEscape(
    colorInside: RGBA = RGBA.white,
    colorOutside: RGBA = RGBA.black
) extends DivergingSeriesColoring

@monocle.macros.Lenses()
case class NormalMap(
    h2: Double Refined NonNaN = refineMV(2.0),
    angle: Double Refined Open[Witness.`0.0`.T, Witness.`6.28318530718`.T] = refineMV(0.78539816339), // todo: maybe define in degree? this is 45°
    colorInside: RGBA = RGBA(0.0, 0.0, 255.0 / 4.0),
    colorLight: RGBA = RGBA.white,
    colorShadow: RGBA = RGBA.black
) extends DivergingSeriesColoring

case class OuterDistance(
    colorInside: RGBA = RGBA(0.0, 0.0, 255.0 / 4.0)
) extends DivergingSeriesColoring

object DivergingSeriesColoring extends CirceCodex {
  val timeEscapeColoring: Prism[DivergingSeriesColoring, TimeEscape]       = GenPrism[DivergingSeriesColoring, TimeEscape]
  val normalMapColoring: Prism[DivergingSeriesColoring, NormalMap]         = GenPrism[DivergingSeriesColoring, NormalMap]
  val outerDistanceColoring: Prism[DivergingSeriesColoring, OuterDistance] = GenPrism[DivergingSeriesColoring, OuterDistance]

  implicit val codecColoring: Codec[DivergingSeriesColoring] = semiauto.deriveConfiguredCodec
}
