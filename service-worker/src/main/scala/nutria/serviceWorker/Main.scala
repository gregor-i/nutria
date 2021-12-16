package nutria.serviceWorker

import org.scalajs.dom.Fetch._
import org.scalajs.dom.Response
import org.scalajs.dom.ServiceWorkerGlobalScope.self
import org.scalajs.dom.{ExtendableEvent, FetchEvent, RequestInfo}

import scala.annotation.nowarn
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.Dynamic
import scala.scalajs.js.JSConverters._
import scala.util.chaining._

@nowarn("cat=other")
object Main {
  val assetCacheName = "assets"

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
      cache <- self.caches.get.open(cacheName).toFuture
      _     <- cache.addAll(files).toFuture
    } yield ()

  def fromCache(cacheName: String, request: RequestInfo): Future[Response] =
    for {
      cache         <- self.caches.get.open(cacheName).toFuture
      maybeResponse <- cache.`match`(request).toFuture
      response      <- Future(maybeResponse.get)
    } yield response

  def invalidateCache(cacheName: String): Future[Boolean] =
    self.caches.get.delete(cacheName).toFuture
}
