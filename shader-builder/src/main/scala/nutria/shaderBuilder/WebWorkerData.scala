package nutria.shaderBuilder

import scala.scalajs.js

class WebWorkerData(val correlationId: WebWorkerData.CorrelationId, val data: String) extends js.Object

object WebWorkerData {
  type CorrelationId = Int

  def apply(correlationId: CorrelationId, data: String): WebWorkerData =
    js.Dynamic
      .literal(
        correlationId = correlationId,
        data = data
      )
      .asInstanceOf[WebWorkerData]
}
