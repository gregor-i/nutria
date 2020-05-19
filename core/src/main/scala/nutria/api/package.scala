package nutria

import nutria.core.{Fractal, FractalTemplate}

package object api {
  type FractalEntity       = Entity[Fractal]
  type FractalEntityWithId = WithId[FractalEntity]

  type FractalTemplateEntity       = Entity[FractalTemplate]
  type FractalTemplateEntityWithId = WithId[FractalTemplateEntity]

}
