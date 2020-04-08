package nutria.frontend.util

import monocle.{Lens, PPrism}

trait LenseUtils {
  def unsafe[A, T](prism: PPrism[T, T, A, A]): Lens[T, A] =
    Lens[T, A](get = prism.getOption(_).get)(set = prism.set)

  def seqAt[A](i: Int): Lens[Seq[A], A] = Lens[Seq[A], A](_.apply(i))(a => seq => seq.updated(i, a))
}

object LenseUtils extends LenseUtils
