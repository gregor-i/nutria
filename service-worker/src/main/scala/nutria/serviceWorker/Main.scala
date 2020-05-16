package nutria.serviceWorker

import org.scalajs.dom
import org.scalajs.dom.experimental.Fetch._
import org.scalajs.dom.experimental._
import org.scalajs.dom.experimental.serviceworkers.ServiceWorkerGlobalScope.self
import org.scalajs.dom.experimental.serviceworkers.{ExtendableEvent, FetchEvent}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.Dynamic
import scala.scalajs.js.JSConverters._
import scala.util.chaining._

object Main {
  val staticCacheName = "nutria-static"
  val staticFiles = js.Array[RequestInfo](
    "/assets/example_DivergingSeries.png",
    "/assets/example_NewtonIteration.png",
    "/assets/fa-solid-900.eot",
    "/assets/fa-solid-900.svg",
    "/assets/fa-solid-900.ttf",
    "/assets/fa-solid-900.woff",
    "/assets/fa-solid-900.woff2",
    "/assets/freestyle.svg",
    "/assets/icon.png",
    "/assets/manifest.json",
    "/assets/nutria.css",
    "/assets/nutria.js",
    "/assets/rendering.svg",
    "/assets/sw.js",
    "/assets/transparent.svg"
  )

  def main(args: Array[String]): Unit = {
    self.addEventListener(
      "install",
      (event: ExtendableEvent) =>
        populateCache(staticCacheName, staticFiles)
          .map(_ => Dynamic.global.console.debug(s"service-worker-build-time: ${buildinfo.BuildInfo.buildTime}"))
          .toJSPromise
          .tap(event.waitUntil(_))
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
        fromCache(staticCacheName, event.request)
          .recoverWith(_ => fetch(event.request).toFuture)
          .toJSPromise
          .tap(event.respondWith(_))
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
