package repo

import nutria.api.{Entity, FractalTemplateEntity}
import nutria.core.Examples

class TemplateRepoSpec
    extends EntityRepoSpec[FractalTemplateEntity](
      repoGetter = _.injector.instanceOf[TemplateRepo],
      e1 = Entity(value = Examples.timeEscape),
      e2 = Entity(value = Examples.newtonIteration)
    )
