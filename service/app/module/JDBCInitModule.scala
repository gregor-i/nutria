package module

import com.google.inject.AbstractModule
import play.api.Logger

// https://bugs.openjdk.java.net/browse/JDK-8146872
class JDBCInitModule extends AbstractModule {
  java.util.ServiceLoader.load(classOf[org.postgresql.Driver])

  override def configure(): Unit = ()
}
