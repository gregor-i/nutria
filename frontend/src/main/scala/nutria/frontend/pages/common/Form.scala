package nutria.frontend.pages.common

import eu.timepit.refined.api.{Refined, Validate}
import eu.timepit.refined.refineV
import mathParser.algebra.SpireLanguage
import monocle.Lens
import nutria.core.languages.StringFunction
import nutria.core.{RGB, RGBA}
import org.scalajs.dom.raw.{HTMLInputElement, HTMLSelectElement}
import snabbdom.{Node, Snabbdom}
import spire.math.Complex

import scala.util.Try

object Form {

  private def inputStyle(label: String, inputs: Node*) =
    Node("div.field.is-horizontal")
      .child(
        Node("div.field-label.is-normal")
          .style("flexGrow", "2")
          .child(Node("label.label").text(label))
      )
      .child(
        Node("div.field-body").child(
          inputs.map(
            input =>
              Node("div.field")
                .child(Node("p.control").child(input))
          )
        )
      )

  def stringFunctionInput[S, V](
      label: String,
      lens: Lens[S, StringFunction[V]]
  )(implicit state: S, update: S => Unit, lang: SpireLanguage[Complex[Double], V]) =
    inputStyle(
      label,
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
    )

  def stringInput[S](
      label: String,
      lens: Lens[S, String]
  )(implicit state: S, update: S => Unit): Node =
    inputStyle(
      label,
      Node("input.input")
        .attr("type", "text")
        .attr("value", lens.get(state))
        .event("change", Snabbdom.event { event =>
          val value = event.target.asInstanceOf[HTMLInputElement].value
          update(lens.set(value)(state))
        })
    )

  def readonlyStringInput(
      label: String,
      value: String
  ): Node =
    inputStyle(
      label,
      Node("input.input")
        .attr("type", "text")
        .attr("disabled", "disabled")
        .attr("value", value)
    )

  def mulitlineStringInput[S](
      label: String,
      lens: Lens[S, String]
  )(implicit state: S, update: S => Unit) =
    inputStyle(
      label,
      Node("textArea.textarea")
        .style("min-height", "400px")
        .event("change", Snabbdom.event { event =>
          val value = event.target.asInstanceOf[HTMLInputElement].value
          update(lens.set(value)(state))
        })
        .text(lens.get(state))
    )

  def intInput[S, T, V](
      label: String,
      lens: Lens[S, Refined[Int, V]]
  )(implicit state: S, update: S => Unit, validate: Validate[Int, V]): Node = {
    inputStyle(
      label,
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
    )
  }

  def intInput[S, T](
      label: String,
      lens: Lens[S, Int]
  )(implicit state: S, update: S => Unit): Node = {
    inputStyle(
      label,
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
    )
  }

  def doubleInput[S, V](
      label: String,
      lens: Lens[S, Double Refined V]
  )(implicit state: S, update: S => Unit, validate: Validate[Double, V]) =
    inputStyle(
      label,
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
    )

  def doubleInput[S](
      label: String,
      lens: Lens[S, Double]
  )(implicit state: S, update: S => Unit) =
    inputStyle(
      label,
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
    )

  def colorInput[S](label: String, lens: Lens[S, RGBA])(implicit state: S, update: S => Unit) =
    inputStyle(
      label,
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
    )

  def tupleDoubleInput[S](
      label: String,
      lens: Lens[S, (Double, Double)]
  )(implicit state: S, update: S => Unit) =
    inputStyle(
      label,
      Node("input.input")
        .attr("type", "number")
        .attr("value", lens.get(state)._1.toString)
        .event("change", Snabbdom.event { event =>
          val value = event.target.asInstanceOf[HTMLInputElement].value.toDouble
          update(lens.modify(t => (value, t._2))(state))
        }),
      Node("input.input")
        .attr("type", "number")
        .attr("value", lens.get(state)._2.toString)
        .event("change", Snabbdom.event { event =>
          val value = event.target.asInstanceOf[HTMLInputElement].value.toDouble
          update(lens.modify(t => (t._1, value))(state))
        })
    )

  def booleanInput[S](label: String, lens: Lens[S, Boolean])(implicit state: S, update: S => Unit) =
    selectInput(
      label = label,
      options = Seq("true" -> true, "false" -> false),
      lens = lens,
      eqFunction = (a: Boolean, b: Boolean) => a == b
    )

  def selectInput[S, A](label: String, options: Seq[(String, A)], lens: Lens[S, A], eqFunction: (A, A) => Boolean)(
      implicit state: S,
      update: S => Unit
  ) = {
    val currentValue = lens.get(state)
    inputStyle(
      label,
      Node("div.select.is-fullwidth")
        .child(
          Node("select")
            .event(
              "change",
              Snabbdom.event { event =>
                val selected = event.target.asInstanceOf[HTMLSelectElement].value
                options
                  .find(_._1 == selected)
                  .map(_._2)
                  .map(lens.set(_)(state))
                  .foreach(update)
              }
            )
            .child(
              options.map {
                case (stringValue, value) =>
                  Node("option")
                    .boolAttr("selected", eqFunction(value, currentValue))
                    .text(stringValue)
              }
            )
        )
    )
  }
}