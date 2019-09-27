package module

import akka.actor.ActorSystem
import io.circe.parser
import javax.inject.{Inject, Singleton}
import nutria.core.FractalEntity
import play.api.Logger
import play.api.inject.{SimpleModule, bind}
import repo.{FractalRepo, FractalRow}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.io.Source

class SetupDataModule extends SimpleModule(
  bind[SystemFractals].toSelf,
  bind[SetupSystemFractals].toSelf.eagerly(),
)

@Singleton
class SystemFractals {
  val systemFractals =
    parser.parse {
      Source.fromResource("systemfractals.json")
        .getLines()
        .mkString("\n")
    }.flatMap(_.as[Vector[FractalEntity]]) match {
      case Right(x) => x
      case Left(error) => throw error
    }
}

private class SetupSystemFractals @Inject()(repo: FractalRepo,
                                            systemFractals: SystemFractals,
                                            actorSystem: ActorSystem)
                                           (implicit ex: ExecutionContext) {
  private val logger = Logger("SetupSystemFractals")

  actorSystem.scheduler.scheduleOnce(100.millis) {
    logger.info("inserting system fractals")
    systemFractals.systemFractals.foreach {
      fractal =>
        repo.save(FractalRow(
          id = FractalEntity.id(fractal),
          maybeFractal = Some(fractal)
        ))
    }
  }
}
