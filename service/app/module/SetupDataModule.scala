package module

import java.io.ByteArrayOutputStream

import akka.actor.ActorSystem
import com.google.inject.AbstractModule
import javax.inject.{Inject, Singleton}
import nutria.core.{DerivedDivergingSeries, Dimensions, DivergingSeries, FractalEntity, NewtonIteration}
import nutria.data.colors.RGBA
import nutria.data.consumers.{CountIterations, NewtonColoring}
import nutria.data.content.LinearNormalized
import nutria.data.image.Image
import nutria.data.sequences.NewtonFractalByString
import repo.{FractalImageRepo, FractalRepo, FractalRow}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import nutria.data.syntax._
import play.api.Logger

class SetupDataModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[Initializer]).asEagerSingleton()
    bind(classOf[FractalImageScheduler]).asEagerSingleton()
  }
}

@Singleton()
private class Initializer @Inject()(repo: FractalRepo) {
  FractalEntity.systemFractals.foreach { fractal =>
    repo.save(FractalRow(
      id = fractal.program.hashCode().toHexString,
      maybeFractal = Some(fractal)
    ))
  }
}

@Singleton()
private class FractalImageScheduler @Inject()(repo: FractalRepo, fractalImageRepo: FractalImageRepo)
                                             (implicit as: ActorSystem, ex: ExecutionContext) {
  as.scheduler.schedule(initialDelay = 0.millis, interval = 1.minute) {
    repo.list()
      .collect { case FractalRow(id, Some(fractal)) => (id, fractal) }
      .filter { case (id, _) => fractalImageRepo.get(id).isEmpty }
      .foreach { case (id, fractal) =>
        Logger.info(s"calculating fractal ${id}")
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
        Logger.info(s"calculated fractal ${id}")
      }
  }
}