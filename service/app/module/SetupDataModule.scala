package module

import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors

import akka.actor.ActorSystem
import javax.inject.Inject
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

class SetupDataModule extends SimpleModule(
  bind[FractalImageScheduler].toSelf.eagerly()
)

private class FractalImageScheduler @Inject()(repo: FractalRepo,
                                              fractalImageRepo: FractalImageRepo) {

  private val executor = Executors.newSingleThreadExecutor()
  private implicit val ex: ExecutionContext = ExecutionContext.fromExecutor(executor)
  private implicit val as: ActorSystem = ActorSystem.create("FractalImageScheduler")

  private val logger = Logger.apply("FractalImageScheduler")

  as.scheduler.scheduleOnce(1.second) {
    logger.info("inserting system fractals")
    FractalEntity.systemFractals.foreach {
      fractal =>
        repo.save(FractalRow(
          id = fractal.program.hashCode().toHexString,
          maybeFractal = Some(fractal)
        ))
    }
  }

  as.scheduler.schedule(initialDelay = 1.second, interval = 1.minute) {
    repo.list()
      .collect { case FractalRow(id, Some(fractal)) => (id, fractal) }
      .filter { case (id, _) => fractalImageRepo.get(id).isEmpty }
      .foreach { case (id, fractal) =>
        logger.info(s"calculating fractal ${id}")
        val img = fractal.view
          .withDimensions(Dimensions(400, 225))
          .withContent(
            fractal.program match {
              case series: DivergingSeries =>
                nutria.data.sequences.DivergingSeries(series)
                  .andThen(CountIterations.double())
                  .andThen(LinearNormalized(0, series.maxIterations))
                  .andThen(f => RGBA(255d * f, 255d * f, 255d * f))
              case newton: NewtonIteration =>
                val f = NewtonFractalByString(newton.function, newton.initial)
                f(newton.maxIterations, newton.threshold, newton.overshoot)
                  .andThen(NewtonColoring.smooth(f))
              case s: DerivedDivergingSeries =>
                nutria.data.sequences.DerivedDivergingSeries(s)
            }
          )
          .multisampled()

        val byteOutputStream = new ByteArrayOutputStream()
        javax.imageio.ImageIO.write(Image.buffer(img), "png", byteOutputStream)
        val bytes = byteOutputStream.toByteArray
        byteOutputStream.close()
        fractalImageRepo.save(id, bytes)
        logger.info(s"calculated fractal ${id}")
      }
  }
}