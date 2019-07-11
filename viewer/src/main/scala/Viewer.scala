import MVC.Model
import MVC.view._
import nutria.data.Defaults
import nutria.data.fractalFamilies.MandelbrotFamily

object Viewer extends Defaults {
  def main(args: Array[String]): Unit = {
    val model = new Model(
      MandelbrotFamily.exampleCalculations.head._2,
      defaultViewport,
      Some(MandelbrotFamily.exampleSequenceConstructor),
      defaultSaveFolder
    )
    new View(model)
  }
}