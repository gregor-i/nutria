package util

import scala.collection.mutable.HashSet

trait Observer {
  def update(caller: Observable): Unit
}

trait Observable {
  val observer = new HashSet[Observer]()

  def addObserver(o: Observer) = observer.add(o)

  def deleteObserver(o: Observer) = observer.remove(o)

  def notifyObservers() = for (o <- observer) o.update(this)

}