package nutria.frontend.common

import com.raquo.snabbdom.simple._
import com.raquo.snabbdom.simple.implicits._
import monocle.Lens
import nutria.frontend.util.SnabbdomHelper.seqNode
import org.scalajs.dom.raw.{HTMLInputElement, HTMLSelectElement}

object Form {

  def inputStyle(label: String, inputs: VNode*) =
    tags.div(attrs.className := "field is-horizontal",
      tags.div(attrs.className := "field-label is-normal",
        styles.flexGrow := "2",
        tags.label(attrs.className := "label", label)
      ),
      tags.div(attrs.className := "field-body",
        seqNode(inputs.map(input =>
          tags.div(attrs.className := "field",
            tags.p(attrs.className := "control",
              input
            )
          )
        ))
      )
    )

  def stringInput[S](label: String, lens: Lens[S, String])
                   (implicit state: S, update: S => Unit) =
    inputStyle(label,
      tags.input(
        attrs.className := "input",
        attrs.`type` := "text",
        attrs.value := lens.get(state),
        events.onChange := {
          event =>
            val value = event.target.asInstanceOf[HTMLInputElement].value
            update(lens.set(value)(state))
        }
      )
    )

  def intInput[S](label: String, lens: Lens[S, Int])
                 (implicit state: S, update: S => Unit) =
    inputStyle(label,
      tags.input(
        attrs.className := "input",
        attrs.`type` := "number",
        attrs.min := "1",
        attrs.value := lens.get(state).toString,
        events.onChange := {
          event =>
            val value = event.target.asInstanceOf[HTMLInputElement].valueAsNumber.toInt
            update(lens.set(value)(state))
        }
      )
    )

  def doubleInput[S](label: String, lens: Lens[S, Double])
                    (implicit state: S, update: S => Unit) =
    inputStyle(label,
      tags.input(
        attrs.className := "input",
        attrs.`type` := "number",
        attrs.value := lens.get(state).toString,
        events.onChange := {
          event =>
            val value = event.target.asInstanceOf[HTMLInputElement].value.toDouble
            update(lens.set(value)(state))
        }
      )
    )

  def tupleDoubleInput[S](label: String, lens: Lens[S, (Double, Double)])
                         (implicit state: S, update: S => Unit) =
    inputStyle(label,
      tags.input(
        attrs.className := "input",
        attrs.`type` := "number",
        attrs.value := lens.get(state)._1.toString,
        events.onChange := {
          event =>
            val value = event.target.asInstanceOf[HTMLInputElement].value.toDouble
            update(lens.modify(t => (value, t._2))(state))
        }
      ),
      tags.input(
        attrs.className := "input",
        attrs.`type` := "number",
        attrs.value := lens.get(state)._2.toString,
        events.onChange := {
          event =>
            val value = event.target.asInstanceOf[HTMLInputElement].value.toDouble
            update(lens.modify(t => (t._1, value))(state))
        }
      )
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
      tags.div(
        attrs.className := "select is-fullwidth",
        tags.select(
          seqNode(options.map(opt => tags.option(opt, attrs.selected := (opt == value)))),
          events.onChange := (event => onChange(event.target.asInstanceOf[HTMLSelectElement].value))
        )
      )
    )


}
