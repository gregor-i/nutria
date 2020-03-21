package model

import org.scalatest.funsuite.AnyFunSuite

import FractalSorting.score

class FractalSortingSpec extends AnyFunSuite {
  test("the score increases with upvotes") {
    assert(score(1, 1) > score(0, 0))
    assert(score(2, 2) > score(1, 1))
    assert(score(3, 3) > score(2, 2))
    assert(score(101, 101) > score(100, 100))
    assert(score(101, 201) > score(100, 200))
  }

  test("decreases with downvotes") {
    assert(score(0, 1) < score(0, 0))
    assert(score(0, 2) < score(0, 1))
    assert(score(0, 3) < score(0, 2))
    assert(score(100, 101) < score(100, 100))
    assert(score(100, 201) < score(100, 200))
  }
}
