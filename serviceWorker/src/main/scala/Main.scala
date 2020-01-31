import org.scalajs.dom.experimental.Fetch._
import org.scalajs.dom.experimental._
import org.scalajs.dom.experimental.serviceworkers.ServiceWorkerGlobalScope.self
import org.scalajs.dom.experimental.serviceworkers.{ExtendableEvent, FetchEvent}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.util.chaining._

object Main {
  val cacheName = "nutria-static"
  val staticFiles = js.Array[RequestInfo](
    "/css/font-awesome.css",
    "/css/bulma.css",
    "/css/nutria.css",
    "/fonts/fontawesome-webfont.woff?v=4.7.0",
    "/fonts/fontawesome-webfont.svg?v=4.7.0",
    "/fonts/fontawesome-webfont.eot?#iefix&v=4.7.0",
    "/fonts/fontawesome-webfont.woff2?v=4.7.0",
    "/fonts/FontAwesome.otf?v=4.7.0",
    "/fonts/fontawesome-webfont.ttf?v=4.7.0",
    "/img/rendering.svg",
    "/img/icon.png",
    "/favicon.ico",
    "/js/nutria.js",
    "/"
  )

  def main(args: Array[String]): Unit = {
    self.addEventListener(
      "install",
      (event: ExtendableEvent) => event.waitUntil(populateCache(cacheName, staticFiles).toJSPromise)
    )

    self.addEventListener(
      "activate",
      (event: ExtendableEvent) =>
        //invalidateCache()
        self.clients.claim()
    )

    self.addEventListener(
      "fetch",
      (event: FetchEvent) => {
        fromCache(cacheName, event.request)
          .recover(_ => fetch(event.request))
          .toJSPromise
          .asInstanceOf[js.Promise[Response]]
          .pipe(event.respondWith(_))

      }
    )
  }

  def populateCache(cacheName: String, files: js.Array[RequestInfo]): Future[Unit] =
    for {
      cache <- self.caches.open(cacheName).toFuture
      _     <- cache.addAll(files).toFuture
    } yield ()

  def fromCache(cacheName: String, request: Request): Future[Response] =
    for {
      cache         <- self.caches.open(cacheName).toFuture
      maybeResponse <- cache.`match`(request).toFuture
      response      <- Future(maybeResponse.get)
    } yield response

  def invalidateCache(cacheName: String): Future[Boolean] =
    self.caches.delete(cacheName).toFuture
}
