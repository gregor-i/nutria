package nutria

import nutria.core.FractalTemplate

package object api {
  type FractalTemplateEntity       = Entity[FractalTemplate]
  type FractalTemplateEntityWithId = WithId[FractalTemplateEntity]
}
