package model

import nutria.core.{FractalEntityWithId, FractalProgram, FreestyleProgram, UpVote, Vote}

object FractalSorting {
  // source:  https://www.evanmiller.org/how-not-to-sort-by-average-rating.html
  private[model] def score(_pos: Int, _n: Int): Double = {
    // note: this is a modification from the original algorithms.
    // pretending to start with a single like, makes dislikes count. ie: score(0, 1) < score(0, 0)
    val pos  = _pos + 1
    val n    = _n + 1
    val z    = 1.96
    val phat = pos.toDouble / n
    (phat + z * z / (2.0 * n) - z * Math.sqrt((phat * (1.0 - phat) + z * z / (4.0 * n)) / n)) / (1.0 + z * z / n)
  }

  private val defaultScore: Double = score(0, 0)

  def ordering(votes: Seq[Vote]): Ordering[FractalEntityWithId] = {
    val acceptanceMap = votes
      .groupBy(_.forFractal)
      .view
      .mapValues { votes =>
        score(votes.count(_.verdict == UpVote), votes.length)
      }

    val acceptanceOrdering = Ordering.Double.TotalOrdering.reverse
      .on[FractalEntityWithId](fractal => acceptanceMap.getOrElse(fractal.id, defaultScore))

    acceptanceOrdering.orElse(orderingByProgram)
  }

  val orderingByProgram: Ordering[FractalEntityWithId] =
    FreestyleProgram.ordering.on(_.entity.program)
}
