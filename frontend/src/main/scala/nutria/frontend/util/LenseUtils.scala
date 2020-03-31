package nutria.frontend.util

import monocle.{Lens, PSetter, Setter}

object LenseUtils {
  //  def primsWithDefault[A, B](p: Prism[B, A], default: A): Lens[B, A] =
  //    Lens[B, A](p.getOption(_).getOrElse(default))(a => s => p.set(a)(s))

  def lookedUp[A, S](value: A, setter: PSetter[S, S, A, A]): Lens[S, A] =
    Lens[S, A](_ => value)(setter.set)

  def withDefault[A, S](p: Lens[S, Option[A]], default: A): Lens[S, A] =
    Lens[S, A](p.get(_).getOrElse(default))(a => p.set(Some(a)))

  def seqAt[A](i: Int): Lens[Seq[A], A] = Lens[Seq[A], A](_.apply(i))(a => seq => seq.updated(i, a))
}
