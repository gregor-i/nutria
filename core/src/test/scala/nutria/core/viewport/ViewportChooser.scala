package nutria.core.viewport

import nutria.core.{Point, Viewport}
import org.scalacheck.Gen.choose

object ViewportChooser {
  val chooseAngle = choose(0d, Math.PI * 2)

  val chooseFromUsefullStartPoints = for {
    a <- chooseAngle
    r <- choose(0d, 5d)
  } yield (Math.sin(a) * r, Math.cos(a) * r)

  val chooseViewport = for {
    origin      <- chooseFromUsefullStartPoints
    originPlusA <- chooseFromUsefullStartPoints
    originPlusB <- chooseFromUsefullStartPoints
  } yield Viewport(
    Point(origin._1, origin._2),
    Point(originPlusA._1 - origin._1, originPlusA._2 - origin._2),
    Point(originPlusB._1 - origin._1, originPlusB._2 - origin._2)
  )
}
