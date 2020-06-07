package repo

import nutria.api.{Entity, FractalImageEntity}
import nutria.core.{Examples, FractalImage}

class ImageRepoSpec
    extends EntityRepoSpec[FractalImageEntity](
      repoGetter = _.injector.instanceOf[ImageRepo],
      e1 = Entity(value = FractalImage.fromTemplate(Examples.timeEscape)),
      e2 = Entity(value = FractalImage.fromTemplate(Examples.newtonIteration))
    )
