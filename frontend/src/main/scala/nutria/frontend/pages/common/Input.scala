package nutria.frontend.pages.common

import mathParser.complex.ComplexLanguage
import monocle.Lens
import nutria.core.languages.StringFunction
import nutria.core.{RGB, RGBA}
import nutria.frontend.GlobalState
import org.scalajs.dom.raw.HTMLInputElement
import snabbdom.{Node, Snabbdom}

import scala.util.Try
import scala.util.chaining._

trait Input[S, V] {
  def node(lens: Lens[S, V]): Node
}

object Input {

  implicit def stringInput[S](implicit globalState: GlobalState, state: S, update: S => Unit): Input[S, String] =
    lens =>
      Node("input.input")
        .attr("type", "text")
        .attr("value", lens.get(state))
        .event("change", Snabbdom.event { event =>
          val value = event.target.asInstanceOf[HTMLInputElement].value
          update(lens.set(value)(state))
        })

  implicit def stringFunctionInput[S, L](
      implicit globalState: GlobalState,
      state: S,
      update: S => Unit,
      lang: ComplexLanguage[L]
  ): Input[S, StringFunction[L]] =
    lens =>
      Node("input.input")
        .attr("type", "text")
        .attr("value", lens.get(state).string)
        .event(
          "change",
          snabbdom.Snabbdom.event { event =>
            val element = event.target.asInstanceOf[HTMLInputElement]
            StringFunction(element.value) match {
              case Some(v) =>
                element.classList.remove("is-danger")
                update(lens.set(v)(state))
              case None =>
                element.classList.add("is-danger")
            }
          }
        )

  implicit def intInput[S](implicit globalState: GlobalState, state: S, update: S => Unit): Input[S, Int] =
    lens =>
      Node("input.input")
        .attr("type", "number")
        .attr("value", lens.get(state).toString)
        .event(
          "change",
          Snabbdom.event { event =>
            val element = event.target.asInstanceOf[HTMLInputElement]
            update(lens.set(element.value.toInt)(state))
          }
        )

  implicit def doubleInput[S](implicit globalState: GlobalState, state: S, update: S => Unit): Input[S, Double] =
    lens =>
      Node("input.input")
        .attr("type", "number")
        .attr("value", lens.get(state).toString)
        .event(
          "change",
          Snabbdom.event { event =>
            val element = event.target.asInstanceOf[HTMLInputElement]
            Try(element.value.toDouble).toEither match {
              case Right(v) =>
                element.classList.remove("is-danger")
                update(lens.set(v)(state))
              case Left(_) =>
                element.classList.add("is-danger")
            }
          }
        )

  implicit def colorInput[S](implicit globalState: GlobalState, state: S, update: S => Unit): Input[S, RGBA] =
    lens =>
      Node("input.input")
        .attr("type", "color")
        .attr("value", RGB.toRGBString(lens.get(state).withoutAlpha))
        .event(
          "change",
          Snabbdom.event { event =>
            val element = event.target.asInstanceOf[HTMLInputElement]
            RGB.parseRGBString(element.value).map(_.withAlpha()).toOption match {
              case Some(v) =>
                element.classList.remove("is-danger")
                update(lens.set(v)(state))
              case None =>
                element.classList.add("is-danger")
            }
          }
        )

  implicit def colorGradientInput[S](implicit globalState: GlobalState, state: S, update: S => Unit): Input[S, Seq[RGBA]] =
    lens => {
      val value = lens.get(state)
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
                update(lens.set(v.toSeq.map(_.withAlpha()))(state))
              case None =>
                element.classList.add("is-danger")
            }
          }
        )
    }
}
