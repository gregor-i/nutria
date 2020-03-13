package nutria.core

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Interval.Open
import eu.timepit.refined.numeric.{NonNaN, Positive}
import eu.timepit.refined.refineMV
import io.circe.Codec
import monocle.Prism
import monocle.macros.GenPrism
import shapeless.Witness

sealed trait DivergingSeriesColoring

@monocle.macros.Lenses()
case class TimeEscape(
    colorInside: RGB = RGB.white,
    colorOutside: RGB = RGB.black
) extends DivergingSeriesColoring

@monocle.macros.Lenses()
case class NormalMap(
    h2: Double Refined NonNaN = refineMV(2.0),
    angle: Double Refined Open[Witness.`0.0`.T, Witness.`6.28318530718`.T] = refineMV(0.78539816339), // todo: maybe define in degree? this is 45°
    colorInside: RGB = RGB(0.0, 0.0, 255.0 / 4.0),
    colorLight: RGB = RGB.white,
    colorShadow: RGB = RGB.black
) extends DivergingSeriesColoring

@monocle.macros.Lenses()
case class OuterDistance(
    colorInside: RGB = RGB(0.0, 0.0, 255.0 / 4.0),
    colorFar: RGB = RGB.black,
    colorNear: RGB = RGB.white,
    distanceFactor: Double Refined Positive = refineMV(1.0)
) extends DivergingSeriesColoring

object DivergingSeriesColoring extends CirceCodex {
  val timeEscapeColoring: Prism[DivergingSeriesColoring, TimeEscape]       = GenPrism[DivergingSeriesColoring, TimeEscape]
  val normalMapColoring: Prism[DivergingSeriesColoring, NormalMap]         = GenPrism[DivergingSeriesColoring, NormalMap]
  val outerDistanceColoring: Prism[DivergingSeriesColoring, OuterDistance] = GenPrism[DivergingSeriesColoring, OuterDistance]

  implicit val codecColoring: Codec[DivergingSeriesColoring] = semiauto.deriveConfiguredCodec
}
