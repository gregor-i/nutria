package nutria.frontend.pages.common

import monocle.Lens
import nutria.frontend.GlobalState
import nutria.frontend.util.SnabbdomUtil
import org.scalajs.dom.raw.{HTMLInputElement, HTMLSelectElement}
import snabbdom.{Node, Snabbdom}

object Form {

  def forLens[S, V](label: String, description: String, lens: Lens[S, V], actions: Seq[(String, S => S)] = Seq.empty)(
      implicit input: Input[S, V],
      globalState: GlobalState,
      update: S => Unit,
      state: S
  ): Node =
    Label(
      label = label,
      description = description,
      actions = actions.map(actionButton(_)),
      node = input.node(lens)
    )

  private def actionButton[S, V](tupled: (String, S => S))(implicit globalState: GlobalState, state: S, update: S => Unit): Node =
    Button.icon(tupled._1, SnabbdomUtil.update[S](tupled._2), round = false)

  def readonlyStringInput(
      label: String,
      value: String,
      actions: Seq[Node] = Seq.empty
  ): Node =
    Label(
      label = label,
      actions = actions,
      node = Node("input.input")
        .attr("type", "text")
        .attr("disabled", "disabled")
        .attr("value", value)
    )

  def mulitlineStringInput[S](
      label: String,
      lens: Lens[S, String],
      actions: Seq[Node] = Seq.empty
  )(implicit globalState: GlobalState, state: S, update: S => Unit) =
    Label(
      label = label,
      actions = actions,
      node = Node("textArea.textarea")
        .style("min-height", "400px")
        .event("change", Snabbdom.event { event =>
          val value = event.target.asInstanceOf[HTMLInputElement].value
          update(lens.set(value)(state))
        })
        .text(lens.get(state))
    )

  def booleanInput[S](
      label: String,
      lens: Lens[S, Boolean],
      actions: Seq[Node] = Seq.empty
  )(implicit globalState: GlobalState, state: S, update: S => Unit) =
    selectInput(
      label = label,
      actions = actions,
      options = Seq("true" -> true, "false" -> false),
      lens = lens,
      eqFunction = (a: Boolean, b: Boolean) => a == b
    )

  def selectInput[S, A](label: String, options: Seq[(String, A)], lens: Lens[S, A], eqFunction: (A, A) => Boolean, actions: Seq[Node] = Seq.empty)(
      implicit state: S,
      update: S => Unit
  ) = {
    val currentValue = lens.get(state)
    Label(
      label = label,
      actions = actions,
      node = Node("div.select.is-fullwidth")
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
