package controller

import io.circe.syntax._
import javax.inject.Inject
import nutria.api.Entity
import nutria.core._
import repo.ImageRepo

import scala.util.Random

class FractalController @Inject() (fractalRepo: ImageRepo, authenticator: Authenticator)
    extends EntityController(fractalRepo, authenticator) {
  def getRandomFractal() = Action {
    val entities = fractalRepo
      .listPublic()
      .collect(fractalRepo.rowToEntity)

    val image: Entity[FractalImage] =
      if (entities.isEmpty) {
        Entity(
          title = "default",
          description = "default",
          value = FractalImage(template = Examples.newtonIteration, viewport = Viewport.aroundZero)
        )
      } else {
        val seed   = java.time.Instant.now.truncatedTo(java.time.temporal.ChronoUnit.DAYS).toEpochMilli
        val random = new Random(seed = seed)

        val images = entities.map(_.entity)
        images(random.nextInt(images.length))
      }

    Ok(image.asJson)
  }
}
