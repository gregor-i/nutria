package module

import java.util.concurrent.Executors

import akka.actor.ActorSystem
import io.circe.parser
import javax.inject.{Inject, Singleton}
import nutria.core._
import nutria.data.colors.RGBA
import nutria.data.consumers.{CountIterations, NewtonColoring}
import nutria.data.content.LinearNormalized
import nutria.data.image.Image
import nutria.data.sequences.NewtonFractalByString
import nutria.data.syntax._
import play.api.Logger
import play.api.inject.{SimpleModule, bind}
import repo.{FractalImageRepo, FractalRepo, FractalRow}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.io.Source

class SetupDataModule extends SimpleModule(
  bind[SystemFractals].toSelf,
  bind[FractalImageScheduler].toSelf.eagerly()
)

@Singleton
class SystemFractals {
  val systemFractals =
    parser.parse {
      Source.fromResource("systemfractals.json")
        .getLines()
        .mkString
    }.flatMap(_.as[Vector[FractalEntity]]).right.get
}

private class FractalImageScheduler @Inject()(repo: FractalRepo,
                                              fractalImageRepo: FractalImageRepo,
                                              systemFractals: SystemFractals,
                                              actorSystem: ActorSystem) {

  private implicit val ex: ExecutionContext =
    ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor())

  private val logger = Logger("FractalImageScheduler")


  actorSystem.scheduler.scheduleOnce(1.second) {
    logger.info("inserting system fractals")
    systemFractals.systemFractals.foreach {
      fractal =>
        repo.save(FractalRow(
          id = FractalEntity.id(fractal),
          maybeFractal = Some(fractal)
        ))
    }
  }

  actorSystem.scheduler.schedule(initialDelay = 1.second, interval = 1.minute) {
    repo.list()
      .collect { case FractalRow(id, Some(fractal)) => (id, fractal) }
      .filter { case (id, _) => fractalImageRepo.get(id).isEmpty }
      .filter(!_._2.program.isInstanceOf[FreestyleProgram])
      .foreach { case (id, fractal) =>
        logger.info(s"calculating fractal ${id}")
        val img = fractal.view
          .withDimensions(Dimensions(400, 225))
          .withContent(
            fractal.program match {
              case series: DivergingSeries =>
                nutria.data.sequences.DivergingSeries(series)
                  .andThen(CountIterations.double())
                  .andThen(LinearNormalized(0, series.maxIterations.value))
                  .andThen(f => RGBA(255d * f, 255d * f, 255d * f))
              case newton: NewtonIteration =>
                val f = NewtonFractalByString(newton.function.string, newton.initial.string)
                f(newton.maxIterations.value, newton.threshold.value, newton.overshoot.value)
                  .andThen(NewtonColoring.smooth(f))
              case s: DerivedDivergingSeries =>
                nutria.data.sequences.DerivedDivergingSeries(s)
              case _:FreestyleProgram => ???
            }
          )
          .multisampled()

        fractalImageRepo.save(id, Image.bytes(img, RGBA.white))
        logger.info(s"calculated fractal ${id}")
      }
  }
}