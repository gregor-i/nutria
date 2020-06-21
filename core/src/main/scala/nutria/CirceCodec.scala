package nutria

import io.circe.generic.extras.Configuration

trait CirceCodec {
  val semiauto: io.circe.generic.extras.semiauto.type = io.circe.generic.extras.semiauto

  implicit val customConfig: Configuration = Configuration.default.withDefaults
}
