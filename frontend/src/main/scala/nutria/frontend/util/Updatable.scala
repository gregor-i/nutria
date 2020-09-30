package nutria.frontend.util

trait Updatable[+S, -P] {
  val state: S
  def update(p: P): Unit
}

object Updatable {
  def apply[S](paramState: S, paramUpdate: S => Unit): Updatable[S, S] = new Updatable[S, S] {
    override val state: S           = paramState
    override def update(p: S): Unit = paramUpdate(p)
  }

  def polymorphic[S, P](paramState: S, paramUpdate: P => Unit): Updatable[S, P] = new Updatable[S, P] {
    override val state: S           = paramState
    override def update(p: P): Unit = paramUpdate(p)
  }
}
