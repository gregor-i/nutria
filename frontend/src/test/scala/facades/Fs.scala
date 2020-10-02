package facades

import scala.scalajs.js
import scala.scalajs.js.Promise
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("fs", JSImport.Namespace)
object Fs extends js.Object {
  def writeFileSync(fileName: String, content: String): Unit = js.native
  def mkdirSync(folderName: String, opts: js.Object): Unit   = js.native
}
