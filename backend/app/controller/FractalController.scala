package controller

import io.circe.syntax._
import javax.inject.Inject
import nutria.core._
import repo.ImageRepo

import scala.util.Random

class FractalController @Inject() (fractalRepo: ImageRepo, authenticator: Authenticator) extends EntityController(fractalRepo, authenticator) {
  def getRandomFractal() = Action {
    val entities = fractalRepo
      .listPublic()
      .collect(fractalRepo.rowToEntity)
    val image: FractalImage =
      if (entities.isEmpty) {
        FractalImage(template = Examples.newtonIteration, viewport = Viewport.aroundZero)
      } else {
        val seed   = java.time.Instant.now.truncatedTo(java.time.temporal.ChronoUnit.DAYS).toEpochMilli
        val random = new Random(seed = seed)

        val images = entities.map(_.entity.value)
        images(random.nextInt(images.length))
      }

    Ok(image.asJson)
  }
}
