package nutria

import nutria.core.RGBA

package object data {
  type Content[A] = nutria.data.content.Content[A]

  type RGB = RGBA
  type Color[A] = A => RGB

  type Image = Content[RGB]
  val Image = nutria.data.image.Image
}
