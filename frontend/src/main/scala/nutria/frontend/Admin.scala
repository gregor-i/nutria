package nutria.frontend

import nutria.core.FractalEntityWithId
import nutria.frontend.util.Untyped
import org.scalajs.dom
import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Admin {
  def setup(): Unit = {
    Untyped(dom.window).cleanFractals = cleanFractals
    Untyped(dom.window).truncateFractals = truncateFractals
    Untyped(dom.window).insertSystemFractals = insertSystemFractals
    Untyped(dom.window).deleteFractal = deleteFractal
    println("Admin Setup completed")
  }

  val cleanFractals: Unit => Future[Unit] = _ =>
    Ajax.post(url = "/admin/clean-fractals")
      .flatMap(_ => onFinished)

  val truncateFractals: Unit => Future[Unit] = _ =>
    Ajax.post(url = "/admin/truncate-fractals")
      .flatMap(_ => onFinished)

  val insertSystemFractals: Unit => Future[Unit] = _ =>
    Ajax.post(url = "/admin/insert-system-fractals")
      .flatMap(_ => onFinished)

  val deleteFractal: String => Future[Unit] = id =>
    Ajax.post(url = s"/admin/delete-fractal/$id")
      .flatMap(_ => onFinished)


  def onFinished = Future(dom.window.location.reload())
}
