package module

import com.google.inject.{AbstractModule, Provider}
import javax.inject.{Inject, Singleton}
import nutria.core.FractalEntity
import repo.{FractalRepo, FractalRow}

class SetupDataModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[Initializer])
      .asEagerSingleton()
  }
}

@Singleton()
private class Initializer @Inject()(repo: FractalRepo) {
  FractalEntity.systemFractals.foreach { fractal =>
    repo.save(FractalRow(
      id = fractal.program.hashCode().toString,
      maybeFractal = Some(fractal)
    ))
  }
}
