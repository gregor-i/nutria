package nutria.frontend.service

import nutria.api.User
import nutria.frontend.service.Service._

import scala.concurrent.Future

object UserService {
  def whoAmI(): Future[Option[User]] =
    Service
      .get("/api/users/me")
      .flatMap(check(200))
      .flatMap(parse[Option[User]])

  def delete(userId: String): Future[Unit] =
    Service
      .delete(s"/api/users/${userId}")
      .flatMap(check(204))
      .map(_ => ())
}
