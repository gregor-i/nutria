package controller

import akka.stream.Materializer
import javax.inject.{Inject, Singleton}
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AccessFilter @Inject()(implicit val mat: Materializer, ex: ExecutionContext) extends Filter {
  val Logger = play.api.Logger("access")

  override def apply(f: RequestHeader => Future[Result])(rh: RequestHeader): Future[Result] = {
    val startTime = System.currentTimeMillis()
    f(rh).map { resp =>
      val endTime = System.currentTimeMillis()
      Logger.info(s"${rh.method} to ${rh.path} returned ${resp.header.status}. ${endTime-startTime}ms.")
      resp
    }
  }
}
