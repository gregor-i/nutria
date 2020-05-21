package facades

import scala.scalajs.js
import scala.scalajs.js.Promise
import scala.scalajs.js.annotation.{JSImport, ScalaJSDefined}

@js.native
@JSImport("fs", JSImport.Namespace)
object Fs extends js.Object {
  def promises: FsPromises = js.native
}

trait FsPromises extends js.Object {
  def writeFile(fileName: String, contenet: String): Promise[_]
  def mkdir(folderName: String, opts: js.Object): Promise[_]
}
