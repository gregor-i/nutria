package nutria

import nutria.core.{Fractal, FractalImage, FractalTemplate}

package object api {
  type FractalEntity       = Entity[Fractal]
  type FractalEntityWithId = WithId[FractalEntity]

  type FractalTemplateEntity       = Entity[FractalTemplate]
  type FractalTemplateEntityWithId = WithId[FractalTemplateEntity]

  type FractalImageEntity       = Entity[FractalImage]
  type FractalImageEntityWithId = WithId[FractalImageEntity]
}
