package nutria.frontend.ui.common

import eu.timepit.refined.api.{Refined, Validate}
import eu.timepit.refined.refineV
import mathParser.algebra.SpireLanguage
import monocle.{Getter, Lens}
import nutria.core.RGBA
import nutria.core.languages.StringFunction
import org.scalajs.dom.raw.{HTMLInputElement, HTMLSelectElement}
import snabbdom.Snabbdom.h
import snabbdom.{Node, Snabbdom, VNode}
import spire.math.Complex

import scala.util.Try

object Form {

  def inputStyle(label: String, inputs: VNode*) =
    Node("div.field.is-horizontal")
      .child(
        Node("div.field-label.is-normal")
          .style("flexGrow", "2")
          .child(Node("label.label").text(label))
      )
      .child(
        Node("div.field-body").child(
          inputs.map(input =>
            Node("div.field")
              .child(Node("p.control").child(input))
            .toVNode
          )
        )
      )
    .toVNode


  def stringFunctionInput[S, V](label: String, lens: Lens[S, StringFunction[V]])
                               (implicit state: S, update: S => Unit, lang: SpireLanguage[Complex[Double], V]) =
    inputStyle(label,
      h("input.input",
        attrs = Seq(
          "type" -> "text",
          "value" -> lens.get(state).string,
        ),
        events = Seq("change" -> snabbdom.Snabbdom.event {
          event =>
            val element = event.target.asInstanceOf[HTMLInputElement]
            StringFunction(element.value) match {
              case Some(v) =>
                element.classList.remove("is-danger")
                update(lens.set(v)(state))
              case None =>
                element.classList.add("is-danger")
            }
        })
      )()
    )

  def stringInput[S](label: String, lens: Lens[S, String])
                       (implicit state: S, update: S => Unit): VNode =
    inputStyle(label,
      Node("input.input")
        .attr("type", "text")
        .attr("value", lens.get(state))
        .event("change", Snabbdom.event {
          event =>
            val value = event.target.asInstanceOf[HTMLInputElement].value
            update(lens.set(value)(state))
        })
        .toVNode
    )

  def mulitlineStringInput[S](label: String, lens: Lens[S, String])
                             (implicit state: S, update: S => Unit) =
    inputStyle(label,
      h("textArea.textarea",
        events = Seq("change" -> Snabbdom.event {
          event =>
            val value = event.target.asInstanceOf[HTMLInputElement].value
            update(lens.set(value)(state))
        })
      )(lens.get(state))
    )

  def intInput[S, T, V](label: String, lens: Lens[S, Refined[Int, V]])
                       (implicit state: S, update: S => Unit, validate: Validate[Int, V]): VNode = {
    inputStyle(label,
      Node("input.input")
        .attr("type", "number")
        .attr("value", lens.get(state).value.toString)
        .event("change", Snabbdom.event {
          event =>
            val element = event.target.asInstanceOf[HTMLInputElement]
            Try(element.value.toInt).toEither
              .flatMap(refineV[V](_)(validate)) match {
              case Right(v) =>
                element.classList.remove("is-danger")
                update(lens.set(v)(state))
              case Left(error) =>
                element.classList.add("is-danger")
            }
        })
        .toVNode
    )
  }

  def doubleInput[S, V](label: String, lens: Lens[S, Double Refined V])
                       (implicit state: S, update: S => Unit, validate: Validate[Double, V]) =
    inputStyle(label,
      h("input.input",
        attrs = Seq(
          "type" -> "number",
          "value" -> lens.get(state).toString,
        ),
        events = Seq("change" -> Snabbdom.event {
          event =>
            val element = event.target.asInstanceOf[HTMLInputElement]
            Try(element.value.toDouble).toEither
              .flatMap(refineV[V](_)(validate)) match {
              case Right(v) =>
                element.classList.remove("is-danger")
                update(lens.set(v)(state))
              case Left(error) =>
                element.classList.add("is-danger")
            }
        })
      )()
    )


  def colorInput[S](label: String, lens: Lens[S, RGBA])
                   (implicit state: S, update: S => Unit) =
    inputStyle(label,
      h("input.input",
        attrs = Seq(
          "type" -> "color",
          "value" -> RGBA.toRGBString(lens.get(state)),
        ),
        events = Seq("change" -> Snabbdom.event {
          event =>
            val element = event.target.asInstanceOf[HTMLInputElement]
            RGBA.parseRGBString(element.value).toOption match {
              case Some(v) =>
                element.classList.remove("is-danger")
                update(lens.set(v)(state))
              case None =>
                element.classList.add("is-danger")
            }
        })
      )()
    )


  def tupleDoubleInput[S](label: String, lens: Lens[S, (Double, Double)])
                         (implicit state: S, update: S => Unit) =
    inputStyle(label,
      h("input.input",
        attrs = Seq(
          "type" -> "number",
          "value" -> lens.get(state)._1.toString,
        ),
        events = Seq("change" -> Snabbdom.event {
          event =>
            val value = event.target.asInstanceOf[HTMLInputElement].value.toDouble
            update(lens.modify(t => (value, t._2))(state))
        })
      )(),
      h("input.input",
        attrs = Seq(
          "type" -> "number",
          "value" -> lens.get(state)._2.toString,
        ),
        events = Seq("change" -> Snabbdom.event {
          event =>
            val value = event.target.asInstanceOf[HTMLInputElement].value.toDouble
            update(lens.modify(t => (t._1, value))(state))
        })
      )()
    )

  def booleanInput[S](label: String, lens: Lens[S, Boolean])
                     (implicit state: S, update: S => Unit) =
    selectInput(label = label,
      options = Seq("true", "false"),
      value = lens.get(state).toString,
      onChange = newValue => update(lens.set(newValue == "true")(state))
    )

  def selectInput(label: String, options: Seq[String], value: String, onChange: String => Unit) =
    inputStyle(label,
      h("div.select is-fullwidth")(
        h("select",
          events = Seq("change" -> Snabbdom.event { event => onChange(event.target.asInstanceOf[HTMLSelectElement].value) })
        )(
          options.map(opt => h("option",
            attrs = if (opt == value) Seq("selected" -> "") else Seq.empty
          )(opt)): _*
        )
      )
    )
}
