package nutria.frontend

import io.circe.syntax._
import nutria.frontend.ui.Ui
import nutria.frontend.util.SnabbdomApp
import org.scalajs.dom
import org.scalajs.dom.Element
import snabbdom.VNode

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.|
import scala.util.{Failure, Success, Try}

class NutriaApp(container: Element, initialState: NutriaState) extends SnabbdomApp {

  var node: Element | VNode = container

  def renderState(state: NutriaState): Unit = {
    Router.stateToUrl(state) match {
      case Some((currentPath, currentSearch)) =>
        val stringSearch = currentSearch.map {
          case (key, value) => s"$key=$value"
        }.mkString("&")
        if (dom.window.location.pathname != currentPath) {
          if (currentSearch.nonEmpty)
            dom.window.history.pushState(state.asJson.noSpaces, "", currentPath + "?" + stringSearch)
          else
            dom.window.history.pushState(state.asJson.noSpaces, "", currentPath)
        } else if (dom.window.location.search != stringSearch) {
          if (currentSearch.nonEmpty)
            dom.window.history.replaceState(state.asJson.noSpaces, "", currentPath + "?" + stringSearch)
          else
            dom.window.history.replaceState(state.asJson.noSpaces, "", currentPath)
        }
      case None => ()
    }

    state match {
      case LoadingState(future) => future.onComplete {
        case Success(newState) => renderState(newState)
        case Failure(exception) => renderState(ErrorState(s"unexpected problem while initializing app: ${exception.getMessage}"))
      }
      case _ => ()
    }

    node = patch(node, Ui(state, renderState))
  }

  dom.window.onpopstate = event => {
    (for {
      jsonString <- Try(event.state.asInstanceOf[String]).toEither
      json <- io.circe.parser.parse(jsonString)
      decoded <- json.as[NutriaState]
    } yield decoded) match {
      case Right(state) => renderState(state)
      case Left(error) => renderState(ErrorState("unexpected problem in window.onpopstate"))
    }
  }

  renderState(initialState)

}
