package module

import com.google.inject.AbstractModule
import play.api.{Configuration, Environment, Logger}
import repo.{ImageRepo, TemplateRepo}

class MigrationModule(environment: Environment, configuration: Configuration) extends AbstractModule with ProviderSyntax {
  private val logger = Logger(this.getClass)

  override def configure() = {
    val provideImageRepo    = getProvider(classOf[ImageRepo])
    val provideTemplateRepo = getProvider(classOf[TemplateRepo])
    bind(classOf[MigrationJob])
      .toProvider(
        for {
          imageRepo    <- provideImageRepo
          templateRepo <- provideTemplateRepo
        } yield new MigrationJob(logger, imageRepo, templateRepo)
      )
      .asEagerSingleton()
  }
}

private class MigrationJob(logger: Logger, imageRepo: ImageRepo, templateRepo: TemplateRepo) {
  logger.info("Starting Migration Job")

  logger.info("Finished Migration Job")
}
