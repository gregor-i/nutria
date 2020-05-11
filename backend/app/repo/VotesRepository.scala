package repo

import anorm._
import javax.inject.Inject
import nutria.api.{DownVote, UpVote, Verdict, Vote}
import play.api.db.Database

class VotesRepository @Inject() (db: Database) {
  private val rowParser: RowParser[Vote] = for {
    forFractal <- SqlParser.str("for_fractal")
    byUser     <- SqlParser.str("by_user")
    verdict    <- SqlParser.int("verdict").map(intToVerdict)
  } yield Vote(forFractal, byUser, verdict)

  def getAll(): Seq[Vote] = db.withConnection { implicit con =>
    SQL"""SELECT * FROM votes"""
      .as(rowParser.*)
  }

  def upsert(vote: Vote): Unit = db.withConnection { implicit con =>
    SQL"""INSERT INTO votes(for_fractal, by_user, verdict)
            VALUES (${vote.forFractal}, ${vote.byUser}, ${verdictToInt(vote.verdict)})
            ON CONFLICT(for_fractal, by_user) DO UPDATE
              SET verdict = ${verdictToInt(vote.verdict)}
       """.execute()
  }

  def delete(forFractal: String, byUser: String): Int =
    db.withConnection { implicit con =>
      SQL"""DELETE FROM votes WHERE for_fractal = ${forFractal} AND by_user = ${byUser}"""
        .executeUpdate()
    }

  private def verdictToInt(verdict: Verdict): Int = verdict match {
    case UpVote   => 1
    case DownVote => -1
  }
  private def intToVerdict(int: Int): Verdict = int match {
    case 1  => UpVote
    case -1 => DownVote
  }
}
