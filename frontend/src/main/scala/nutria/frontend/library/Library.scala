package nutria.frontend.library

import com.raquo.snabbdom.simple._
import nutria.frontend.util.SnabbdomApp
import org.scalajs.dom.Element

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.|

class Library(container: Element) extends SnabbdomApp {

  var node: Element | VNode = container

  def renderState(state: LibraryState): Unit = {
    node = patch(node, LibraryUi.render(state, renderState))
  }

  LibraryService.loadFractals()
    .foreach(fractals => renderState(LibraryState(fractals)))
}
