package nutria.core.consumers

import nutria.core.fractal.SequenceChooser
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Gen.{Choose, choose}
import org.specs2.{ScalaCheck, Specification}
import spire.math.{Complex, Real}
import spire.implicits._
import org.scalacheck.Prop.forAll
import spire.algebra.Field


class GaussianIntegerSpec extends Specification with ScalaCheck {
  def is =
    s2"""
                  Gaussian Integer Trap:
                    For any given complex number c, this trap calculates the minimal distance to a complex number t with t.real and t.imag is natual number.

                ${e1(algo)}
                ${e1(fast)}
                ${e2(algo)}
                ${e2(fast)}
                $e3
                ${e4(algo).pendingUntilFixed("i don't know ...")}
                ${e4(fast).pendingUntilFixed("i don't know ...")}
    """

  type S = Double
  type F = Complex[S] => S
  val field: Field[S] = implicitly

  def genComplex(r: Double): Gen[Complex[S]] = for {
    a <- choose(0, Math.PI * 2).map(field.fromDouble)
    r <- choose(0, r).map(field.fromDouble)
  } yield Complex.polar(r, a)

  implicit val arbComplex: Arbitrary[Complex[S]] = Arbitrary(genComplex(1000))



  val algo: F = c => (c - c.round).abs

  val fast: F = c => {
    val x = c.real - c.real.round
    val y = c.imag - c.imag.round
    (x * x + y * y).sqrt()
  }

  def e1(f: F) = forAll(genComplex(1000)) {
    c =>
      val a = f(c)
     (for {ix <- -1 to 1
        iy <- -1 to 1
      } yield (c.floor + Complex(field.fromInt(ix), field.fromInt(iy)) - c).abs).forall(_ >= a)
  }

  def e2(f:F) = forAll(genComplex(1000)) {
    c => fast(c) <= field.fromInt(2).sqrt / field.fromInt(2)
  }

  def e3 = forAll(genComplex(1000)) {
    c => fast(c) === algo(c)
  }

  def e4(f: F) = forAll {
    (c: Complex[S], ix: Int, iy: Int) =>
      f(c) === f(c + Complex[S](field.fromInt(ix), field.fromInt(iy)))
  }
}
