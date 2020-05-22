package nutria.serviceWorker

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
  val assetCacheName = "nutria-assets"

  val assets =
    buildinfo.BuildInfo.assetFiles
      .split("\n")
      .map[RequestInfo](fileName => "/assets/" + fileName)
      .toJSArray

  val startUrl: RequestInfo = "/"

  def main(args: Array[String]): Unit = {
    self.addEventListener(
      "install",
      (event: ExtendableEvent) =>
        (for {
          _ <- invalidateCache(assetCacheName)
          _ <- populateCache(assetCacheName, assets)
          _ <- populateCache(assetCacheName, js.Array(startUrl))
          _ = Dynamic.global.console.debug(s"service-worker-build-time: ${buildinfo.BuildInfo.buildTime}")
        } yield ()).toJSPromise
          .tap(event.waitUntil(_))
    )

    self.addEventListener("activate", (_: ExtendableEvent) => self.clients.claim())

    self.addEventListener(
      "fetch",
      (event: FetchEvent) => {
        fromCache(assetCacheName, event.request)
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

  def fromCache(cacheName: String, request: RequestInfo): Future[Response] =
    for {
      cache         <- self.caches.open(cacheName).toFuture
      maybeResponse <- cache.`match`(request).toFuture
      response      <- Future(maybeResponse.get)
    } yield response

  def invalidateCache(cacheName: String): Future[Boolean] =
    self.caches.delete(cacheName).toFuture
}
