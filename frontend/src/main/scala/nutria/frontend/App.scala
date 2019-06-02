package nutria
package frontend

import com.raquo.snabbdom.simple.VNode
import nutria.frontend.util.SnabbdomApp
import org.scalajs.dom.Element

import scala.scalajs.js.|

class App(container: Element) extends SnabbdomApp {

  var node: Element | VNode = container

  def renderState(state: State): Unit = {
    node = patch(node, Ui.render(state, renderState))
  }

  renderState(State.initial)
}
