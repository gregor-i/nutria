package controller

import controllers.Assets
import javax.inject.Inject
import play.api.mvc.InjectedController

import scala.concurrent.ExecutionContext

class UiController @Inject() (assets: Assets)(implicit ex: ExecutionContext) extends InjectedController {
  def frontend(path: String) = asset("nutria.html", "/public/html")

  def asset(file: String, folder: String) = assets.at(folder, file)

  def serviceWorker(file: String, folder: String) =
    Action.async(
      rq =>
        assets
          .at(folder, file)
          .apply(rq)
          .map(_.withHeaders("Service-Worker-Allowed" -> "/"))
    )

}
