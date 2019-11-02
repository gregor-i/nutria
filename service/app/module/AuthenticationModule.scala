package module

import module.auth.{AuthenticationController, AuthenticationDummy, AuthenticationGoogle}
import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment, Mode}

class AuthenticationModule extends Module {
  def bindings(environment: Environment, conf: Configuration): collection.Seq[Binding[_]] =
    Seq(
      environment.mode match {
        case Mode.Prod =>
          bind[AuthenticationController].to[AuthenticationGoogle]
        case _ =>
          bind[AuthenticationController].to[AuthenticationDummy]
      }
    )
}
