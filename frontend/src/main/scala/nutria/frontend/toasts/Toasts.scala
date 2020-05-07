package nutria.frontend.toasts

import org.scalajs.dom
import snabbdom.{Node, Snabbdom, SnabbdomFacade, VNode}

object Toasts {
  private var counter: Int             = 0
  private var toasts: List[ToastState] = List.empty

  private var node: Option[VNode] = None

  private def render(): Unit = {
    val ui = Node("toast-bar")
      .child(toasts.map { toast =>
        Node("div.notification")
          .key(toast.id)
          .classes(toast.`class`)
          .child(Node("button.delete").event("click", Snabbdom.event(_ => removeToast(toast.id))))
          .text(toast.text)
          .style("transition", "0.5s")
          .hook("insert", Snabbdom.hook { vnode =>
            dom.window.setTimeout(() => vnode.elm.get.style.opacity = "0", 2000)
            dom.window.setTimeout(() => removeToast(toast.id), 2500)
            ()
          })
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

  def successToast(text: String): Unit = addToast(text, "is-success")
  def dangerToast(text: String): Unit  = addToast(text, "is-danger")
  def warningToast(text: String): Unit = addToast(text, "is-warning")

  private def addToast(text: String, `class`: String): Unit = {
    val id    = { counter += 1; counter }
    val toast = ToastState(text, `class`, id)
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

private case class ToastState(text: String, `class`: String, id: Int)
