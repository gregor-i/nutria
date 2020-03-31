package nutria.frontend.util

import monocle.{Lens, POptional, PPrism, PSetter}

object LenseUtils {
  def lookedUp[A, S](value: A, setter: PSetter[S, S, A, A]): Lens[S, A] =
    Lens[S, A](_ => value)(setter.set)

  def subclass[A, S, T](lens: POptional[S, S, T, T], prism: PPrism[T, T, A, A], value: A): Lens[S, A] =
    lookedUp(value, lens.composeSetter(prism.asSetter))

  def subclass[A, S, T](lens: Lens[S, T], prism: PPrism[T, T, A, A], value: A): Lens[S, A] =
    lookedUp(value, lens.composePrism(prism).asSetter)

  def seqAt[A](i: Int): Lens[Seq[A], A] = Lens[Seq[A], A](_.apply(i))(a => seq => seq.updated(i, a))
}
