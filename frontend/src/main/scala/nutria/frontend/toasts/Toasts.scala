package nutria.frontend.toasts

import nutria.frontend.ExecutionContext
import org.scalajs.dom
import snabbdom.{Node, Snabbdom, SnabbdomFacade, VNode}

import scala.concurrent.Future
import scala.util.Try

private sealed trait ToastState {
  def id: Int
}
private case class FireAndForgetToast(text: String, toastType: ToastType, id: Int) extends ToastState
private case class FutureToast[A](progressText: String, progress: Future[A], onComplete: Try[A] => (ToastType, String), id: Int) extends ToastState {
  type State = A
}

object Toasts extends ExecutionContext {
  private var counter: Int             = 0
  private var toasts: List[ToastState] = List.empty

  private var node: Option[VNode] = None

  private def render(): Unit = {
    val ui = Node("toast-bar")
      .child(toasts.map {
        case toast: FireAndForgetToast =>
          notification(toast.toastType, toast.text)
            .key(toast.id)
            .child(Node("button.delete").event("click", Snabbdom.event(_ => removeToast(toast.id))))
            .style("transition", "0.5s")
            .hook("insert", Snabbdom.hook { vnode =>
              dom.window.setTimeout(() => vnode.elm.get.style.opacity = "0", 2000)
              dom.window.setTimeout(() => removeToast(toast.id), 2500)
              ()
            })

        case toast: FutureToast[_] =>
          toast.progress.value match {
            case Some(value) =>
              val (toastType, text) = toast.asInstanceOf[FutureToast[toast.State]].onComplete(value.asInstanceOf[Try[toast.State]])

              notification(toastType, text)
                .key(toast.id)
                .child(Node("button.delete").event("click", Snabbdom.event(_ => removeToast(toast.id))))
                .style("transition", "0.5s")
                .hook(
                  "postpatch",
                  Snabbdom.hook { vnode =>
                    dom.window.setTimeout(() => vnode.elm.get.style.opacity = "0", 2000)
                    dom.window.setTimeout(() => removeToast(toast.id), 2500)
                    ()
                  }
                )

            case None =>
              notification(Info, toast.progressText)
                .key(toast.id)
                .hook("insert", Snabbdom.hook { _ =>
                  toast.progress.onComplete(_ => render())
                  ()
                })
          }
      })
      .toVNode

    node match {
      case None =>
        val container = dom.document.createElement("toast-bar")
        dom.document.body.appendChild(container)
        node = Some(patch(container, ui))
      case Some(vnode) =>
        node = Some(patch(vnode, ui))
    }
  }

  def notification(toastType: ToastType, text: String): Node =
    Node("div.notification")
      .classes(toastType.`class`)
      .child(
        Node("div.media")
          .child(
            Node("figure.media-left")
              .child(
                Node("span.icon")
                  .child(
                    Node("i.fas.fa-lg").classes(toastType.iconClasses: _*)
                  )
              )
          )
          .child(
            Node("div.media-content").text(text)
          )
      )

  def successToast(text: String): Unit = addToast(text, Success)
  def dangerToast(text: String): Unit  = addToast(text, Danger)
  def warningToast(text: String): Unit = addToast(text, Warning)

  def addToast(text: String, toastType: ToastType): Int = {
    val id    = { counter += 1; counter }
    val toast = FireAndForgetToast(text, toastType, id)
    toasts = toast :: toasts
    render()
    id
  }

  def futureToast[A](progressText: String, progress: Future[A], onComplete: Try[A] => (ToastType, String)): Unit = {
    val id    = { counter += 1; counter }
    val toast = FutureToast(progressText, progress, onComplete, id)
    toasts = toast :: toasts
    render()
  }

  def removeToast(id: Int): Unit = {
    toasts = toasts.filter(_.id != id)
    render()
  }

  val patch: SnabbdomFacade.PatchFunction = Snabbdom.init(
    classModule = true,
    styleModule = true,
    eventlistenersModule = true
  )

}
