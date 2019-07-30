package module

import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment, Logger}

import scala.collection.JavaConverters._

// https://bugs.openjdk.java.net/browse/JDK-8146872
class JDBCInitModule extends Module {

  val logger = Logger.apply(classOf[JDBCInitModule])

  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    def isLoaded =
      java.sql.DriverManager.getDrivers
        .asScala
        .exists(_.isInstanceOf[org.postgresql.Driver])

    if (!isLoaded) {
      java.util.ServiceLoader.load(classOf[org.postgresql.Driver])
      logger.info("loading org.postgresql.Driver")
      if (!isLoaded)
        throw new Exception("could not load org.postgresql.Driver")
    } else {
      logger.info("org.postgresql.Driver was loaded")
    }
    Seq.empty
  }

}
