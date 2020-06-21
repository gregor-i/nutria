package nutria.frontend.util

import org.scalajs.dom

import scala.concurrent.{Future, Promise}

object AsyncUtil {
  def sleep(millis: Int): Future[Unit] = {
    val p = Promise[Unit]()
    dom.window.setTimeout(() => p.success(()), millis)
    p.future
  }
}
