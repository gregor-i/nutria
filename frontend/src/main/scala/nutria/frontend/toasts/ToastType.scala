package nutria.frontend.toasts

sealed abstract class ToastType(val `class`: String, val iconClasses: Seq[String])
case object Success extends ToastType("is-success", Seq("fa-check-circle"))
case object Danger  extends ToastType("is-danger", Seq("fa-times-circle"))
case object Warning extends ToastType("is-warning", Seq("fa-exclamation-circle"))
case object Info    extends ToastType("is-info", Seq("fa-spinner", "fa-spin"))
