package module

import akka.actor.ActorSystem
import com.google.inject.AbstractModule
import javax.inject.Provider
import play.api.libs.ws.WSClient
import play.api.{Configuration, Environment, Logger}

import scala.concurrent.duration._

class NoSleepModule(environment: Environment, configuration: Configuration) extends AbstractModule {
  private val logger = Logger(this.getClass)

  override def configure() = {

    val maybeUrl = configuration.getOptional[String]("NoSleepModule.url")
    maybeUrl match {
      case None => logger.info("No 'NoSleepModule.url' defined")
      case Some(url) =>
        val tickInterval = configuration.getOptional[FiniteDuration]("NoSleepModule.tickInterval").getOrElse(10.minutes)
        logger.info(s"Starting poller on '$url' with interval '$tickInterval'")
        val as        = ActorSystem("NoSleepModule")
        val provideWs = getProvider(classOf[WSClient])
        bind(classOf[Poller])
          .toProvider(new Provider[Poller] {
            override def get(): Poller = new Poller(provideWs.get(), url, tickInterval, as)
          })
          .asEagerSingleton()
    }
  }
}

private class Poller(ws: WSClient, url: String, tickInterval: FiniteDuration, as: ActorSystem) {
  private implicit val ex = as.dispatcher

  as.scheduler.scheduleAtFixedRate(initialDelay = 0.seconds, interval = tickInterval)(
    () => ws.url(url).get()
  )
}
