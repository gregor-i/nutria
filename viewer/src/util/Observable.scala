package util

trait Observable[A] {
  _ : A =>
  var observer = Set[A => Unit]()

  def addObserver(op: A => Unit) = observer += op

  def notifyObservers() = observer.foreach(_.apply(this))
}
