package module

import play.api.{Configuration, Environment, Mode}
import play.api.inject.{Binding, Module}

class AuthenticationModule extends Module  {
   def bindings(environment: Environment, conf: Configuration): collection.Seq[Binding[_]] =
     environment.mode match {
       case Mode.Prod =>
         val clientId: String = conf.get[String]("auth.google.clientId")
         val clientSecret: String = conf.get[String]("auth.google.clientSecret")
         val callbackUrl: String = conf.get[String]("auth.google.callbackUrl") // "http://localhost:9000/auth/google"

       case _ => ???
     }
}
