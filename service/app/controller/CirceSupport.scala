package controller

import io.circe.Json
import play.api.http.{ContentTypeOf, ContentTypes, Writeable}
import play.api.mvc.Codec

trait CirceSupport {
  implicit def writableOf_Json(implicit codec: Codec): Writeable[Json] = {
    Writeable(a => codec.encode(a.noSpaces))
  }

  implicit val contentTypeOf_Json: ContentTypeOf[Json] = {
    ContentTypeOf(Some(ContentTypes.JSON))
  }
}
