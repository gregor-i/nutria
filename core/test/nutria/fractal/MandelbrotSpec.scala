package nutria.fractal

import nutria.fractal.SequenceChooser._
import org.scalacheck.Gen.choose
import org.scalacheck.Prop.forAll
import org.specs2.{ScalaCheck, Specification}

class MandelbrotSpec extends Specification with ScalaCheck {
  def is =
    s2"""
       All points inside the cardioid start an infinite sequence                   $insideCardioid
       All points inside the P2 Circle start an initite sequence                   $insideP2
       The sequence starting at (0, 0) repeats infinitely                          $zero

       All sequences start at (0,0)                                                $startAtZero
       All sequences starting at (x,y) are at (x,y) after 1 iteration              $afterOneIteration
       All sequences starting at p with p.abs < 2 terminate directly               $staringOutside
       All sequences starting at different points as (0,0) is next not idenpotent  $noRepeating
    """

  val iterations = 1000

  private def sequence = SequenceConstructor[Mandelbrot.Sequence]


  def insideCardioid = forAll(chooseFromTheInsideOfTheCardioid) {
    case (x: Double, y: Double) =>
      sequence(x, y, iterations).size() === iterations
  }

  def insideP2 = forAll(chooseFromPointsInsideOfP2) {
    case (x, y) =>
      sequence(x, y, iterations).size() === iterations
  }

  def zero = forAll(choose(0, iterations)) {
    (i: Int) =>
      val seq = sequence(0, 0, iterations)
      for (_ <- 0 until i) seq.next()
      seq.publicX === 0 and seq.publicY === 0
  }

  def startAtZero = forAll(chooseFromUsefullStartPoints) {
    case (x: Double, y: Double) =>
      val seq = sequence(x, y, iterations)
      seq.publicX === 0 and seq.publicY === 0
  }

  def afterOneIteration = forAll(chooseFromUsefullStartPoints) {
    case (x, y) =>
      val seq = sequence(x, y, iterations)
      seq.next()
      seq.publicX === x and seq.publicY === y
  }

  def staringOutside = forAll(chooseFromPointsOutsideOfTheEscapeRadius) {
    case (x, y) =>
      val seq = sequence(x, y, iterations)
      seq.next()
      seq.hasNext === false
  }

  def noRepeating = forAll(chooseFromUsefullStartPoints) {
    case (x, y) =>
      val seq = sequence(x, y, iterations)
      val oldState = seq.public
      seq.next()
      oldState !== seq.public
  }
}