package nutria.frontend.pages
package common

import monocle.Lens
import nutria.frontend.util.{SnabbdomUtil, Updatable}
import org.scalajs.dom.raw.{HTMLInputElement, HTMLSelectElement}
import snabbdom.components.Button
import snabbdom.{Event, Node}

object Form {

  def forLens[S, V](label: String, description: String, lens: Lens[S, V], actions: Seq[(String, S => S)] = Seq.empty)(
      implicit input: Input[V],
      updatable: Updatable[S, S]
  ): Node =
    Label(
      label = label,
      description = description,
      actions = actions.map(actionButton(_)),
      node = input.node(Updatable.composeLens(updatable, lens))
    )

  private def actionButton[S, V](tupled: (String, S => S))(implicit updatable: Updatable[S, S]): Node =
    Button.icon(tupled._1, SnabbdomUtil.modify[S](tupled._2), round = false)

  def readonlyStringInput(
      label: String,
      value: String,
      actions: Seq[Node] = Seq.empty
  ): Node =
    Label(
      label = label,
      actions = actions,
      node = "input.input"
        .attr("type", "text")
        .attr("disabled", "disabled")
        .attr("value", value)
    )

  def mulitlineStringInput[S](
      label: String,
      lens: Lens[S, String],
      actions: Seq[Node] = Seq.empty
  )(implicit updatable: Updatable[S, S]) =
    Label(
      label = label,
      actions = actions,
      node = "textArea.textarea"
        .style("min-height", "400px")
        .event[Event]("change", event => {
          val value = event.target.asInstanceOf[HTMLInputElement].value
          updatable.update(lens.set(value)(updatable.state))
        })
        .text(lens.get(updatable.state))
    )

  def booleanInput[S](
      label: String,
      lens: Lens[S, Boolean],
      actions: Seq[Node] = Seq.empty
  )(implicit updatable: Updatable[S, S]) =
    selectInput(
      label = label,
      actions = actions,
      options = Seq("true" -> true, "false" -> false),
      lens = lens,
      eqFunction = (a: Boolean, b: Boolean) => a == b
    )

  def selectInput[S, A](label: String, options: Seq[(String, A)], lens: Lens[S, A], eqFunction: (A, A) => Boolean, actions: Seq[Node] = Seq.empty)(
      implicit updatable: Updatable[S, S]
  ) = {
    val currentValue = lens.get(updatable.state)
    Label(
      label = label,
      actions = actions,
      node = "div.select.is-fullwidth"
        .child(
          "select"
            .event[Event](
              "change",
              event => {
                val selected = event.target.asInstanceOf[HTMLSelectElement].value
                options
                  .find(_._1 == selected)
                  .map(_._2)
                  .map(lens.set(_)(updatable.state))
                  .foreach(updatable.update)
              }
            )
            .child(
              options.map {
                case (stringValue, value) =>
                  "option"
                    .boolAttr("selected", eqFunction(value, currentValue))
                    .text(stringValue)
              }
            )
        )
    )
  }
}
