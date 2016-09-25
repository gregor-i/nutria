package util

trait Observer {
  def update(caller: Observable): Unit
}

trait Observable {
  var observer = Set[Observer]()

  def addObserver(o: Observer) = observer += o

  def addObserver(op: () => Unit) = observer += new Observer{
    override def update(caller: Observable): Unit = op()
  }


  def deleteObserver(o: Observer) = observer -= o

  def notifyObservers() = for (o <- observer) o.update(this)
}