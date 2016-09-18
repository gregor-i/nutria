import MVC.model._
import MVC.view._

object Viewer {
  def main(args: Array[String]): Unit = {
    val model = new Model()
    new View(model)
  }
}


