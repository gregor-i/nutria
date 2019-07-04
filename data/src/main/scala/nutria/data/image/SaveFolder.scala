package nutria.data.image

import java.io.File

case class SaveFolder(path: String) {
  require(path.endsWith(File.separator), s"per convention all save folder path have to end with an '${File.separator}'")

  def inFolder(file: String): File = {
    require(!file.contains(File.separator))
    new File(path + file)
  }

  def /~(file: String): File = inFolder(file)

  def subFolder(folder: String): SaveFolder = {
    require(!folder.contains(File.separator))
    SaveFolder(path + folder + File.separator)
  }

  def /(folder: String): SaveFolder = subFolder(folder)
}


object SaveFolder {
  val defaultSaveFolder: SaveFolder = SaveFolder("." + File.separator)
}
