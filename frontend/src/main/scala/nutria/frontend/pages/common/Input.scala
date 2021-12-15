package nutria.frontend.pages
package common

import mathParser.complex.{Complex, ComplexLanguage}
import nutria.core.languages.StringFunction
import nutria.core.{RGB, RGBA}
import nutria.frontend.util.{ComplexLenses, Updatable}
import org.scalajs.dom.raw.HTMLInputElement
import snabbdom.{Event, Node}

import scala.util.Try
import scala.util.chaining._

trait Input[S] {
  def node(updatable: Updatable[S, S]): Node
}

object Input {
  implicit val stringInput: Input[String] =
    updatable =>
      "input.input"
        .attr("type", "text")
        .prop("value", updatable.state)
        .event[Event](
          "change",
          event => {
            val value = event.target.asInstanceOf[HTMLInputElement].value
            updatable.update(value)
          }
        )

  implicit def stringFunctionInput[L](implicit
      lang: ComplexLanguage[L]
  ): Input[StringFunction[L]] =
    updatable =>
      "input.input"
        .attr("type", "text")
        .prop("value", updatable.state.string)
        .event[Event](
          "change",
          event => {
            val element = event.target.asInstanceOf[HTMLInputElement]
            StringFunction(element.value) match {
              case Some(v) =>
                element.classList.remove("is-danger")
                updatable.update(v)
              case None =>
                element.classList.add("is-danger")
            }
          }
        )

  implicit val intInput: Input[Int] =
    updatable =>
      "input.input"
        .attr("type", "number")
        .prop("value", updatable.state.toString)
        .event[Event](
          "change",
          event => {
            val element = event.target.asInstanceOf[HTMLInputElement]
            updatable.update(element.value.toInt)
          }
        )

  implicit val doubleInput: Input[Double] =
    updatable =>
      "input.input"
        .attr("type", "number")
        .prop("value", updatable.state.toString)
        .event[Event](
          "change",
          event => {
            val element = event.target.asInstanceOf[HTMLInputElement]
            Try(element.value.toDouble).toEither match {
              case Right(v) =>
                element.classList.remove("is-danger")
                updatable.update(v)
              case Left(_) =>
                element.classList.add("is-danger")
            }
          }
        )

  implicit val complexInput: Input[Complex] =
    updatable =>
      "div.field-body"
        .child(
          doubleInput
            .node(Updatable.composeLens(updatable, ComplexLenses.real))
            .classes("field")
        )
        .child(
          doubleInput
            .node(Updatable.composeLens(updatable, ComplexLenses.imag))
            .classes("field")
        )

  implicit val colorInput: Input[RGBA] =
    updatable =>
      "input.input"
        .attr("type", "color")
        .prop("value", RGB.toRGBString(updatable.state.withoutAlpha))
        .event[Event](
          "change",
          event => {
            val element = event.target.asInstanceOf[HTMLInputElement]
            RGB.parseRGBString(element.value).map(_.withAlpha()).toOption match {
              case Some(v) =>
                element.classList.remove("is-danger")
                updatable.update(v)
              case None =>
                element.classList.add("is-danger")
            }
          }
        )

  implicit val colorGradientInput: Input[Seq[RGBA]] =
    updatable => {
      val value = updatable.state
      "input.input.color-gradient-input"
        .attr("type", "text")
        .prop("value", value.map(_.withoutAlpha).map(RGB.toRGBString).mkString(" "))
        .style("background-image", s"linear-gradient(to right, ${value.map(_.withoutAlpha).map(RGB.toRGBString).mkString(", ")})")
        .event[Event](
          "change",
          event => {
            val element = event.target.asInstanceOf[HTMLInputElement]
            element.value
              .split("\\s")
              .map(RGB.parseRGBString)
              .pipe(tries => Try(tries.map(_.get)))
              .toOption match {
              case Some(v) =>
                element.classList.remove("is-danger")
                updatable.update(v.toSeq.map(_.withAlpha()))
              case None =>
                element.classList.add("is-danger")
            }
          }
        )
    }
}
