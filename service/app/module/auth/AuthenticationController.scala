package module.auth

import play.api.mvc.{Action, AnyContent, BaseController}

trait AuthenticationController extends BaseController {
  def authenticate(): Action[AnyContent]

  def logout(): Action[AnyContent]
}
