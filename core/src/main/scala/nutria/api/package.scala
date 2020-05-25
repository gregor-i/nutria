package nutria

import nutria.core.{FractalImage, FractalTemplate}

package object api {
  type FractalTemplateEntity       = Entity[FractalTemplate]
  type FractalTemplateEntityWithId = WithId[FractalTemplateEntity]

  type FractalImageEntity       = Entity[FractalImage]
  type FractalImageEntityWithId = WithId[FractalImageEntity]
}
