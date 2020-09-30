package nutria.frontend.pages.common

import mathParser.complex.ComplexLanguage
import nutria.core.languages.StringFunction
import nutria.core.{RGB, RGBA}
import nutria.frontend.util.Updatable
import org.scalajs.dom.raw.HTMLInputElement
import snabbdom.{Node, Snabbdom}

import scala.util.Try
import scala.util.chaining._

trait Input[S] {
  def node(updatable: Updatable[S, S]): Node
}

object Input {
  implicit val stringInput: Input[String] =
    updatable =>
      Node("input.input")
        .attr("type", "text")
        .attr("value", updatable.state)
        .event("change", Snabbdom.event { event =>
          val value = event.target.asInstanceOf[HTMLInputElement].value
          updatable.update(value)
        })

  implicit def stringFunctionInput[L](
      implicit lang: ComplexLanguage[L]
  ): Input[StringFunction[L]] =
    updatable =>
      Node("input.input")
        .attr("type", "text")
        .attr("value", updatable.state.string)
        .event(
          "change",
          snabbdom.Snabbdom.event { event =>
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
      Node("input.input")
        .attr("type", "number")
        .attr("value", updatable.state.toString)
        .event(
          "change",
          Snabbdom.event { event =>
            val element = event.target.asInstanceOf[HTMLInputElement]
            updatable.update(element.value.toInt)
          }
        )

  implicit val doubleInput: Input[Double] =
    updatable =>
      Node("input.input")
        .attr("type", "number")
        .attr("value", updatable.state.toString)
        .event(
          "change",
          Snabbdom.event { event =>
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

  implicit val colorInput: Input[RGBA] =
    updatable =>
      Node("input.input")
        .attr("type", "color")
        .attr("value", RGB.toRGBString(updatable.state.withoutAlpha))
        .event(
          "change",
          Snabbdom.event { event =>
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
      Node("input.input.color-gradient-input")
        .attr("type", "text")
        .attr("value", value.map(_.withoutAlpha).map(RGB.toRGBString).mkString(" "))
        .style("background-image", s"linear-gradient(to right, ${value.map(_.withoutAlpha).map(RGB.toRGBString).mkString(", ")})")
        .event(
          "change",
          snabbdom.Snabbdom.event { event =>
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
