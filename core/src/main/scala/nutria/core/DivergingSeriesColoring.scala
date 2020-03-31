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
    colorInside: RGBA = RGB.white.withAlpha(),
    colorOutside: RGBA = RGB.black.withAlpha()
) extends DivergingSeriesColoring

@monocle.macros.Lenses()
case class NormalMap(
    h2: Double Refined NonNaN = refineMV(2.0),
    angle: Double Refined Open[Witness.`0.0`.T, Witness.`6.28318530718`.T] = refineMV(0.78539816339), // todo: maybe define in degree? this is 45°
    colorInside: RGBA = RGB(0.0, 0.0, 255.0 / 4.0).withAlpha(),
    colorLight: RGBA = RGB.white.withAlpha(),
    colorShadow: RGBA = RGB.black.withAlpha()
) extends DivergingSeriesColoring

@monocle.macros.Lenses()
case class OuterDistance(
    colorInside: RGBA = RGB(0.0, 0.0, 255.0 / 4.0).withAlpha(),
    colorFar: RGBA = RGB.white.withAlpha(),
    colorNear: RGBA = RGB.black.withAlpha(),
    distanceFactor: Double Refined Positive = refineMV(1.0)
) extends DivergingSeriesColoring

object DivergingSeriesColoring extends CirceCodex {
  val timeEscapeColoring    = GenPrism[DivergingSeriesColoring, TimeEscape]
  val normalMapColoring     = GenPrism[DivergingSeriesColoring, NormalMap]
  val outerDistanceColoring = GenPrism[DivergingSeriesColoring, OuterDistance]

  implicit val codecColoring: Codec[DivergingSeriesColoring] = semiauto.deriveConfiguredCodec
}
