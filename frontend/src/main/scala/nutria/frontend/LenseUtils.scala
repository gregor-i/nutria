package nutria.frontend

import monocle.{Lens, Optional, Prism, Setter}

object LenseUtils {
//  def primsWithDefault[A, B](p: Prism[B, A], default: A): Lens[B, A] =
//    Lens[B, A](p.getOption(_).getOrElse(default))(a => s => p.set(a)(s))

  def lookedUp[A, S](value: A, setter:Setter[S, A]): Lens[S, A] =
    Lens[S, A](_ => value)(setter.set)

  def optional[A, S](lens: Lens[S, Option[A]]): Optional[S, A] =
    Optional[S, A](lens.get)(a => lens.set(Some(a)))

  def withDefault[A, S](p: Lens[S, Option[A]], default: A): Lens[S, A] =
    Lens[S, A](p.get(_).getOrElse(default))(a => p.set(Some(a)))
}
