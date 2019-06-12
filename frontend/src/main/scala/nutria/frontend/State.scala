package nutria.frontend

import nutria.core.Viewport
import nutria.data.Defaults
import nutria.frontend.shaderBuilder.{Iteration, JuliaSetIteration, MandelbrotIteration, TricornIteration}
import spire.math.Complex

import scala.util.Try

case class State(view: Viewport,
                 maxIterations: Int,
                 escapeRadius: Double,
                 antiAliase: Int,
                 shaded: Boolean,
                 iteration: Iteration,
                 dragStartPosition: Option[(Double, Double)],
                )

object State {
  def initial = State(
    view = Defaults.defaultViewport,
    maxIterations = 200,
    escapeRadius = 100,
    antiAliase = 2,
    shaded = true,
    iteration = MandelbrotIteration,
    dragStartPosition = None,
  )

  trait QueryParamCodex[A] {
    def fromString(string: String): Option[A]

    def tostring(a: A): String
  }

  def decode[A: QueryParamCodex](s: String): Option[A] = implicitly[QueryParamCodex[A]].fromString(s)

  def encode[A: QueryParamCodex](a: A): String = implicitly[QueryParamCodex[A]].tostring(a)

  implicit object IntQueryParamCodex extends QueryParamCodex[Int] {
    override def fromString(string: String): Option[Int] = Try(string.toInt).toOption

    override def tostring(a: Int): String = a.toString
  }

  implicit object DoubleQueryParamCodex extends QueryParamCodex[Double] {
    override def fromString(string: String): Option[Double] = Try(string.toDouble).toOption

    override def tostring(a: Double): String = a.toString
  }

  implicit object BooleanQueryParamCodex extends QueryParamCodex[Boolean] {
    override def fromString(string: String): Option[Boolean] = Try(string.toBoolean).toOption

    override def tostring(a: Boolean): String = a.toString
  }

  implicit object ViewQueryParamCodex extends QueryParamCodex[Viewport] {
    override def fromString(string: String): Option[Viewport] = {
      val R = "(.*),(.*),(.*),(.*),(.*),(.*)".r
      string match {
        case R(f1, f2, f3, f4, f5, f6) => Try {
          Viewport((f1.toDouble, f2.toDouble),
            (f3.toDouble, f4.toDouble),
            (f5.toDouble, f6.toDouble))
        }.toOption
        case _ => None
      }
    }

    override def tostring(view: Viewport): String = Seq(
      encode(view.origin._1),
      encode(view.origin._2),
      encode(view.A._1),
      encode(view.A._2),
      encode(view.B._1),
      encode(view.B._2),
    ).mkString(",")
  }

  implicit object IterationQueryParamCodex extends QueryParamCodex[Iteration]{
    private val jsR = "JuliaSetIteration\\((.*),(.*)\\)".r
    override def fromString(string: String): Option[Iteration] =
      string match {
        case "MandelbrotIteration" => Some(MandelbrotIteration)
        case "TricornIteration" => Some(TricornIteration)
        case jsR(real, imag) => Try(JuliaSetIteration(Complex(real.toDouble, imag.toDouble))).toOption
      }

    override def tostring(a: Iteration): String =  a match {
      case JuliaSetIteration(c) => s"JuliaSetIteration(${encode(c.real)},${encode(c.imag)})"
      case _ => a.toString
    }
  }


  def toQueryString(state: State): String =
    Seq(
      "view" -> encode(state.view),
      "maxIterations" -> encode(state.maxIterations),
      "escapeRadius" -> encode(state.escapeRadius),
      "antiAliase" -> encode(state.antiAliase),
      "shaded" -> encode(state.shaded),
      "iteration" -> encode(state.iteration),
    ).map { case (key, value) => s"$key=$value" }
      .mkString("?", "&", "")

  def fromQueryString(query: String): State = {
    val R = "(.*)=(.*)".r
    val map = query.dropWhile(_ == '?').split("&").flatMap {
      case R(key, value) => Some(key -> value)
      case _ => None
    }.toMap

    initial.copy(
      view = map.get("view").flatMap(decode[Viewport]).getOrElse(initial.view),
      maxIterations = map.get("maxIterations").flatMap(decode[Int]).getOrElse(initial.maxIterations),
      escapeRadius = map.get("escapeRadius").flatMap(decode[Double]).getOrElse(initial.escapeRadius),
      antiAliase = map.get("antiAliase").flatMap(decode[Int]).getOrElse(initial.antiAliase),
      shaded = map.get("shaded").flatMap(decode[Boolean]).getOrElse(initial.shaded),
      iteration = map.get("iteration").flatMap(decode[Iteration]).getOrElse(initial.iteration)
    )
  }
}

