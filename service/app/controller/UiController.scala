package controller

import controllers.Assets
import javax.inject.Inject
import play.api.mvc.InjectedController

class UiController @Inject()(assets: Assets) extends InjectedController {
  def slash = Action(PermanentRedirect("/library"))

  def library = Action(Ok(views.html.Nutria()))

  def explorer = Action(Ok(views.html.Nutria()))

  def asset(file: String, folder: String) = assets.at(folder, file)
}

