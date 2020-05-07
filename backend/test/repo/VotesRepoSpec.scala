package repo

import java.util.UUID

import nutria.core.{DownVote, UpVote, Vote}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

class VotesRepoSpec extends AnyFunSuite with Matchers with GuiceOneAppPerSuite with BeforeAndAfterEach {
  def repo = app.injector.instanceOf[VotesRepository]

  override def beforeEach = {
    repo.getAll().foreach(row => repo.delete(row.forFractal, row.byUser))
  }

  val userId1    = UUID.randomUUID().toString
  val userId2    = UUID.randomUUID().toString
  val fractalId1 = UUID.randomUUID().toString
  val fractalId2 = UUID.randomUUID().toString

  val vote1up   = Vote(forFractal = fractalId1, byUser = userId1, verdict = UpVote)
  val vote1down = Vote(forFractal = fractalId1, byUser = userId1, verdict = DownVote)
  val vote2up   = Vote(forFractal = fractalId2, byUser = userId2, verdict = UpVote)
  val vote2down = Vote(forFractal = fractalId2, byUser = userId2, verdict = DownVote)

  test("save") {
    repo.upsert(vote1up)
    repo.getAll() should contain(vote1up)

    repo.upsert(vote1down)
    repo.getAll() should not contain (vote1up)
    repo.getAll() should contain(vote1down)
  }

  test("get") {
    repo.upsert(vote1up)
    repo.upsert(vote2up)

    repo.getAll() should contain(vote1up)
    repo.getAll() should contain(vote2up)
    repo.getAll() should have length (2)
  }

  test("delete") {
    repo.upsert(vote1up)
    repo.getAll() should contain(vote1up)

    repo.delete(vote1up.forFractal, vote1up.byUser)
    repo.getAll() should not contain (vote1up)
  }

}
