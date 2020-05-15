package nutria.frontend

trait ExecutionContext {
  implicit val ex: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
}
