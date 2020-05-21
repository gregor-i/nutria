package nutria.frontend.pages.common

import eu.timepit.refined.api.{Refined, Validate}
import eu.timepit.refined.refineV
import monocle.Lens
import nutria.core.{RGB, RGBA}
import nutria.core.languages.{CLang, StringFunction}
import org.scalajs.dom.raw.HTMLInputElement
import snabbdom.{Node, Snabbdom}

import scala.util.Try

trait Input[S, V] {
  def node(lens: Lens[S, V]): Node
}

object Input {

  implicit def stringInput[S](implicit state: S, update: S => Unit): Input[S, String] =
    lens =>
      Node("input.input")
        .attr("type", "text")
        .attr("value", lens.get(state))
        .event("change", Snabbdom.event { event =>
          val value = event.target.asInstanceOf[HTMLInputElement].value
          update(lens.set(value)(state))
        })

  implicit def stringFunctionInput[S, L](implicit state: S, update: S => Unit, lang: CLang[L]): Input[S, StringFunction[L]] =
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

  implicit def intInput[S](implicit state: S, update: S => Unit): Input[S, Int] =
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

  implicit def refinedIntInput[S, V](implicit state: S, update: S => Unit, validate: Validate[Int, V]): Input[S, Int Refined V] =
    lens =>
      Node("input.input")
        .attr("type", "number")
        .attr("value", lens.get(state).value.toString)
        .event(
          "change",
          Snabbdom.event { event =>
            val element = event.target.asInstanceOf[HTMLInputElement]
            Try(element.value.toInt).toEither
              .flatMap(refineV[V](_)(validate)) match {
              case Right(v) =>
                element.classList.remove("is-danger")
                update(lens.set(v)(state))
              case Left(error) =>
                element.classList.add("is-danger")
            }
          }
        )

  implicit def refinedDoubleInput[S, V](implicit state: S, update: S => Unit, validate: Validate[Double, V]): Input[S, Double Refined V] =
    lens =>
      Node("input.input")
        .attr("type", "number")
        .attr("value", lens.get(state).toString)
        .event(
          "change",
          Snabbdom.event { event =>
            val element = event.target.asInstanceOf[HTMLInputElement]
            Try(element.value.toDouble).toEither
              .flatMap(refineV[V](_)(validate)) match {
              case Right(v) =>
                element.classList.remove("is-danger")
                update(lens.set(v)(state))
              case Left(error) =>
                element.classList.add("is-danger")
            }
          }
        )

  implicit def doubleInput[S](implicit state: S, update: S => Unit): Input[S, Double] =
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
              case Left(error) =>
                element.classList.add("is-danger")
            }
          }
        )

  implicit def colorInput[S](implicit state: S, update: S => Unit): Input[S, RGBA] =
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
}
