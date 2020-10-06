package module

import akka.actor.ActorSystem
import com.google.inject.AbstractModule
import javax.inject.Provider
import play.api.libs.ws.WSClient
import play.api.{Configuration, Environment, Logger}

import scala.concurrent.duration._

class NoSleepModule(environment: Environment, configuration: Configuration) extends AbstractModule with ProviderSyntax {
  private val logger = Logger(this.getClass)

  override def configure() = {
    val maybeUrl = configuration.getOptional[String]("NoSleepModule.url")
    maybeUrl match {
      case None => logger.info("No 'NoSleepModule.url' defined")
      case Some(url) =>
        logger.info(s"Starting poller on '$url'")
        val provideWs = getProvider(classOf[WSClient])
        bind(classOf[Poller])
          .toProvider(
            for {
              ws <- provideWs
            } yield new Poller(ws, url)
          )
          .asEagerSingleton()
    }
  }
}

private class Poller(ws: WSClient, url: String) {
  private implicit val as = ActorSystem("NoSleepModule")
  private implicit val ex = as.dispatcher

  as.scheduler.scheduleAtFixedRate(initialDelay = 0.seconds, interval = 10.minutes)(
    () => ws.url(url).get()
  )
}
