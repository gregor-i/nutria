package nutria.data.consumers

import nutria.data.colors.{HSV, RGB}
import nutria.data.sequences.Newton
import nutria.data.{Color, DoubleSequence}
import spire.implicits._
import spire.math.Complex

import scala.util.control.NonFatal

object NewtonColoring {
  def apply(): DoubleSequence => RGB =
    seq => try {
      val i = seq.size
      val (x, y) = seq.next()
      val a = Math.atan2(x, y)

      val H = (a / Math.PI * 180 + 360) % 360
      val S = Math.exp(-i / 25d)
      val V = S

      HSV.HSV2RGB(H, S, V)
    } catch {
      case NonFatal(_) => RGB.white
    }

  def smooth(newton: Newton): newton.Sequence => RGB =
    NewtonIteration(newton) andThen NewtonIteration.colorTheResult((0, 0))
}


object NewtonIteration {
  sealed trait NewtonResult
  case class ConvergedToRoot(iterations: Double, root: (Double, Double)) extends NewtonResult
  case object FailedByException extends NewtonResult
  case object NotConverged extends NewtonResult

  def apply[A <: Newton](newton: A): A#Sequence => NewtonResult =
    seq => try {
      var i = 1
      var last = (0d, 0d)
      var now = (0d, 0d)
      while (seq.hasNext) {
        last = now
        now = seq.next()
        i += 1
      }

      val s = {
        val fnow = newton.f(Complex(now._1, now._2), seq.lambda).abs
        val flast = newton.f(Complex(last._1, last._2), seq.lambda).abs
        if (fnow == 0.0)
          i - 1
        else
          i - Math.log(seq.threshold / fnow) / Math.log(flast / fnow)
      }

      if (newton.f(Complex(now._1, now._2), seq.lambda).abs < seq.threshold)
        ConvergedToRoot(s, now)
      else
        NotConverged
    } catch {
      case NonFatal(ex) => FailedByException
    }

  def colorTheResult(origin: (Double, Double)): Color[NewtonResult] = {
    case FailedByException => RGB.black
    case NotConverged => RGB.black
    case ConvergedToRoot(iterations, root) =>
      val angle = Math.atan2(root._1 - origin._1, root._2 - origin._2)
      val H = (angle / Math.PI * 180 + 360) % 360
      val S = Math.exp(-iterations / 25d)
      val V = S
      HSV.HSV2RGB(H, S, V)
  }
}
