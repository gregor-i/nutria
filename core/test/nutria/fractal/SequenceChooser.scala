package nutria.fractal

import nutria.fractal.techniques.CardioidTechniques
import org.scalacheck.Gen._

object SequenceChooser {
  val chooseAngle = choose(0d, Math.PI * 2)

  val chooseFromUsefullStartPoints = for {
    a <- chooseAngle
    r <- choose(0d, 5d)
  } yield (Math.sin(a) * r, Math.cos(a) * r)

  val chooseFromPointsOutsideOfTheEscapeRadius = for {
    a <- chooseAngle
    r <- choose(2d, 10d)
  } yield (Math.sin(a) * r, Math.cos(a) * r)

  val chooseFromTheCardioidContour = chooseAngle.map(CardioidTechniques.contour)

  val chooseFromTheInsideOfTheCardioid = for {
    (cx, cy) <- chooseFromTheCardioidContour
    f <- choose(0d, 0.95)
  } yield (cx * f, cy * f)

  val chooseFromPointsInsideOfP2 = for {
    a <- chooseAngle
    r <- choose(0d, 1d)
  } yield (Math.sin(a) * r * 0.25 - 1, Math.cos(a) * r * 0.25)

}
