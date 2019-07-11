package nutria

package object data {
  type Content[A] = nutria.data.content.Content[A]

  type RGB = nutria.data.colors.RGBA
  type Color[A] = A => RGB

  type Image = Content[RGB]
  val Image = nutria.data.image.Image
}
