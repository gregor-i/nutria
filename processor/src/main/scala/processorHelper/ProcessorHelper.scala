package processorHelper

import scala.concurrent.duration.{DurationLong, FiniteDuration}

sealed trait Result
case class Made(executionTime: FiniteDuration) extends Result
case object Skipped extends Result
case class RequirementFailed(e: IllegalArgumentException) extends Result
case class UnexpectedException(e: Exception) extends Result

trait Task {
  def name: String
  def skipCondition: Boolean
  def execute(): Unit
}

trait NoSkip {
  _: Task =>
  override def skipCondition = false
}

trait Skip {
  _: Task =>
  override def skipCondition = true
}

trait ProcessorHelper {
  def statusPrints: Boolean = true

  def executeTask(task: Task): Result =
    try {
      if (task.skipCondition) {
        Skipped
      } else {
        val startTime = System.currentTimeMillis()
        task.execute()
        val endTime = System.currentTimeMillis()
        Made((endTime - startTime).millis)
      }
    } catch {
      case e: IllegalArgumentException => RequirementFailed(e)
      case e: Exception => UnexpectedException(e)
    }

  def executeAllTasks(tasks: Traversable[Task]): Seq[Result] = {
    val results = for (task <- tasks.toSeq)
      yield {
        val result = executeTask(task)
        if (statusPrints) println(s"completed ${task.name} with status $result")
        result
      }

    if (statusPrints) {
      println("Tasks completed:")
      println(s"Made:                ${results.count(_.isInstanceOf[Made])}")
      println(s"Skipped:             ${results.count(_ == Skipped)}")
      println(s"RequirementFailed:   ${results.count(_.isInstanceOf[RequirementFailed])}")
      println(s"UnexpectedException: ${results.count(_.isInstanceOf[UnexpectedException])}")

      for (exception <- results.collect { case UnexpectedException(exception) => exception }.toSet[Exception]) {
        println(s"Unexpected Exception: ${exception.getMessage}")
        exception.printStackTrace()
      }
    }
    results.toSeq
  }
}
