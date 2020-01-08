package nutria.core

import io.circe.Codec
import io.circe.generic.JsonCodec

case class Vote(forFractal: String, byUser: String, verdict: Verdict)

case class VoteStatistic(upvotes: Int, downvotes: Int, yourVerdict: Option[Verdict])

sealed trait Verdict
case object DownVote extends Verdict
case object UpVote   extends Verdict

object Verdict extends CirceCodex {
  implicit val codec: Codec[Verdict] = semiauto.deriveConfiguredCodec
}

object VoteStatistic extends CirceCodex {
  implicit val codec: Codec[VoteStatistic] = semiauto.deriveConfiguredCodec

  val empty: VoteStatistic = VoteStatistic(0, 0, None)
}
