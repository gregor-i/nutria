import java.io.File

import nutria.core.image.SaveFolder

trait DefaultSaveFolder {
  val defaultSaveFolder:SaveFolder = SaveFolder("." + File.separator)
}

object DefaultSaveFolder extends DefaultSaveFolder