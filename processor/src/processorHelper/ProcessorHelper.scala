/*
 * Copyright (C) 2016  Gregor Ihmor & Merlin Göttlinger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package processorHelper

import scala.concurrent.duration.{Duration, DurationLong}

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

trait NoSkip { _: Task =>
  override def skipCondition = false
}

trait Skip { _: Task =>
  override def skipCondition = true
}

trait ProcessorHelper {
  def statusPrints: Boolean = true

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
