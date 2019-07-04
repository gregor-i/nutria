package nutria.data.content

import nutria.core.Dimensions
import nutria.core.viewport.HasDimensions

trait Content[A] extends HasDimensions {
  self =>
  def apply(x: Int, y: Int): A

  def map[B](f: A => B): Content[B] = new Content[B] {
    override def apply(x: Int, y: Int): B = f(self(x, y))
    override val dimensions: Dimensions = self.dimensions
  }

  def cached: CachedContent[A] = new CachedContent[A](this)
}

class CachedContent[A](val values: Seq[Seq[A]], val dimensions: Dimensions) extends Content[A] {
  def this(content: Content[A]) =
    this(
      (0 until content.width).par.map(x => (0 until content.height).map(y => content(x, y))).seq,
      content.dimensions)

  override def apply(x: Int, y: Int): A = values(x)(y)

  override def map[B](f: A => B): CachedContent[B] = new CachedContent[B](values.map(_.map(f)), dimensions)

  override def cached: CachedContent[A] = this
}
