package nutria.frontend.util

import monocle.{Lens, Optional, PPrism}

trait LenseUtils {
  def unsafe[A, T](prism: PPrism[T, T, A, A]): Lens[T, A] =
    Lens[T, A](get = prism.getOption(_).get)(set = prism.set)

  def unsafeOptional[A, T](optional: Optional[A, T]): Lens[A, T] =
    Lens[A, T](get = optional.getOption(_).get)(set = optional.set)

  def seqAt[A](i: Int): Lens[Seq[A], A] = Lens[Seq[A], A](_.apply(i))(a => seq => seq.updated(i, a))

  def seqWhere[A](value: A): Lens[Vector[A], A] =
    Lens[Vector[A], A](get = _ => value)(
      set = newValue =>
        oldValues =>
          oldValues.map {
            case `value`    => newValue
            case otherValue => otherValue
          }
    )
}

object LenseUtils extends LenseUtils
