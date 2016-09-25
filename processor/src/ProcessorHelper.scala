import java.io.File

import scala.concurrent.duration.Duration
import scala.concurrent.duration.DurationLong

sealed trait Result
case class Made(executionTime:Duration) extends Result
case object Skipped extends Result
case object RequirementFailed extends Result
case class UnexpectedException(e:Exception) extends Result

trait Task{
  def name: String
  def skipCondition: Boolean
  def execute(): Unit
}

trait NoSkip {
  _: Task =>
  override def skipCondition = false
}

trait ProcessorHelper {
  def rootFolder: String
  def statusPrints: Boolean

  def fileInRootFolder(file: String): File = new File(rootFolder + file)

  def executeTask(task: Task): Result =
    try {
      if(task.skipCondition){
        Skipped
      }else {
        val startTime = System.currentTimeMillis()
        task.execute()
        val endTime = System.currentTimeMillis()
        Made((endTime - startTime).millis)
      }
    } catch {
      case _: IllegalArgumentException => RequirementFailed
      case e: Exception => UnexpectedException(e)
    }

  def executeAllTasks(tasks: Traversable[Task]): Seq[Result] = {
    val results = for (task <- tasks)
      yield {
        val result = executeTask(task)
        if (statusPrints) println(s"completed ${task.name} with status $result")
        result
      }

    if (statusPrints) {
      val countsByResult = results.groupBy(identity).mapValues(_.size).withDefaultValue(0)
      println("Tasks completed:")
      println(s"Made:                ${results.count(_.isInstanceOf[Made])}")
      println(s"Skipped:             ${countsByResult(Skipped)}")
      println(s"RequirementFailed:   ${countsByResult(RequirementFailed)}")
      println(s"UnexpectedException: ${results.count(_.isInstanceOf[UnexpectedException])}")

      for(exception <- results.collect{case UnexpectedException(exception) => exception}.toSet[Exception]) {
        println(s"Unexpected Exception: ${exception.getMessage}")
        exception.printStackTrace()
      }
    }
    results.toSeq
  }
}
