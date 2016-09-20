import java.io.File

import scala.concurrent.duration.Duration
import scala.concurrent.duration.DurationLong

sealed trait Result
case class Made(executionTime:Duration) extends Result
case object Skipped extends Result
case object RequirementFailed extends Result
case object UnexpectedException extends Result

trait ProcessorHelper {
  type Task = (() => Boolean, () => Unit)

  def rootFolder: String
  def statusPrints: Boolean

  def fileInRootFolder(file: String): File = new File(rootFolder + file)

  def make(task: Task): Result =
    try {
      val (skipCondition, operation) = task
      if(skipCondition()){
        Skipped
      }else {
        val startTime = System.currentTimeMillis()
        operation()
        val endTime = System.currentTimeMillis()
        Made((endTime - startTime).millis)
      }
    } catch {
      case _: IllegalArgumentException => RequirementFailed
      case _: Exception => UnexpectedException
    }

  def makeAll(tasks: Set[Task]): Set[Result] = {
    val results = for (task <- tasks)
      yield {
        val result = make(task)
        if (statusPrints) println(s"completed ??? with status $result")
        result
      }

    if (statusPrints) {
      val countsByResult = results.groupBy(identity).mapValues(_.size).withDefaultValue(0)
      println("Tasks completed:")
      println(s"Made:                ${results.count(_.isInstanceOf[Made])}")
      println(s"Skipped:             ${countsByResult(Skipped)}")
      println(s"RequirementFailed:   ${countsByResult(RequirementFailed)}")
      println(s"UnexpectedException: ${countsByResult(UnexpectedException)}")
    }
    results
  }
}
