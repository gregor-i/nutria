package controller

import controllers.Assets
import javax.inject.Inject
import play.api.mvc.InjectedController

class UiController @Inject() (assets: Assets) extends InjectedController {
  def frontend(path: String) = asset("nutria.html", "/public/html")

  def asset(file: String, folder: String) = assets.at(folder, file)
}
