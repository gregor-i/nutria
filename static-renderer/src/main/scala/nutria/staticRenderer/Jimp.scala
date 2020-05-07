package nutria.staticRenderer

import scala.scalajs.js
import scala.scalajs.js.Promise
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("jimp", JSImport.Namespace)
object Jimp extends js.Object {
  def read(read: js.Object): Promise[JimpImage] = js.native
}

@js.native
trait JimpImage extends js.Object {
  def write(file: String): Unit = js.native
}
