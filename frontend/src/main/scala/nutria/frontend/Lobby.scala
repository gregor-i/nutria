package nutria
package frontend

import com.raquo.snabbdom.simple._
import com.raquo.snabbdom.simple.implicits._
import io.circe.{Decoder, parser}
import monocle.Lens
import monocle.macros.GenLens
import nutria.core._
import nutria.frontend.shaderBuilder.FractalRenderer
import nutria.frontend.util.{Hooks, SnabbdomApp, SnabbdomHelper}
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.{Element, XMLHttpRequest}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js.|
import scala.util.control.NonFatal

class Lobby(container: Element) extends SnabbdomApp {

  var node: Element | VNode = container

  def renderState(state: LobbyState): Unit = {
    node = patch(node, LobbyUi.render(state, renderState))
  }

  LobbyService.loadFractals()
    .foreach(fractals => renderState(LobbyState(fractals)))
}

//@monocle.macros.Lenses()
case class LobbyState(programs: Vector[FractalEntity],
                      edit: Option[FractalEntity] = None)

object LobbyState {
  val edit: Lens[LobbyState, Option[FractalEntity]] = GenLens[LobbyState](_.edit)
}

object LobbyService {
  def loadFractals(): Future[Vector[FractalEntity]] =
    Ajax.get(s"/api/fractals")
      .map(checkAndParse[Vector[FractalEntity]](200))
      .recover {
        case NonFatal(error) =>
          println(error)
          Vector.empty
      }

  private def checkAndParse[A: Decoder](expected: Int)(req: XMLHttpRequest): A =
    parser.parse(req.responseText).flatMap(_.as[A]).right.get

}

object LobbyUi extends SnabbdomHelper {
  def render(implicit state: LobbyState, update: LobbyState => Unit): VNode = {
    tags.div(
      header,
      tags.div(
        attrs.className := "lobby-tile-list",
        seqNode(state.programs.map(renderProgramTile)),
        seqNode(Seq.fill(5)(dummyTile))
      ),
      renderPopup(),
    )
  }

  def header: VNode =
    tags.div(
      attrs.className := "top-bar",
      tags.div(
        tags.img(
          styles.height := "100%",
          attrs.src := "/img/icon.png",
        ),
        tags.span(
          "Nutria Fractal Library"
        )
      )
    )

  def renderPopup()
                 (implicit state: LobbyState, update: LobbyState => Unit): Option[VNode] =
    state.edit.map { fractal =>
      tags.div(
        attrs.className := "modal is-active",
        tags.div(
          attrs.className := "modal-background",
          events.onClick := (() => update(state.copy(edit = None))),
        ),
        tags.div(
          attrs.className := "modal-content lobby-tile-list",
          renderEditProgramTile(fractal)
        )
      )
    }

  def renderProgramTile(fractal: FractalEntity)
                       (implicit state: LobbyState, update: LobbyState => Unit): VNode =
    tags.build("article")(
      tags.a(
        attrs.href := Viewer.url(fractal.program),
        tags.canvas(
          attrs.widthAttr := 400,
          attrs.heightAttr := 225,
          Hooks.insertHook { node =>
            val canvas = node.elm.get.asInstanceOf[Canvas]
            FractalRenderer.render(canvas, fractal.program, false)
          }
        )
      ),
      attrs.title := fractal.description,
      tags.footer(
        tags.a(
          attrs.href := Viewer.url(fractal.program),
          "explore",
        ),
        tags.a(
          events.onClick := (() => update(state.copy(edit = Some(fractal)))),
          "edit",
        ),
        tags.a(
          fractal.reference.map(url => attrs.href := url),
          "documenation"
        ),
        tags.a(
          "delete"
        )
      )
    )

  def renderEditProgramTile(fractal: FractalEntity)
                           (implicit state: LobbyState, update: LobbyState => Unit): VNode = {
    val lensEdit = LenseUtils.lookedUp(fractal, LenseUtils.optional(LobbyState.edit).asSetter)
    tags.build("article")(
      tags.a(
        attrs.href := Viewer.url(fractal.program),
        tags.canvas(
          attrs.widthAttr := 400,
          attrs.heightAttr := 225,
          Hooks.insertHook { node =>
            val canvas = node.elm.get.asInstanceOf[Canvas]
            FractalRenderer.render(canvas, fractal.program, false)
          },
          Hooks.postPatchHook { (_, newNode) =>
            val canvas = newNode.elm.get.asInstanceOf[Canvas]
            FractalRenderer.render(canvas, fractal.program, false)
          }
        )
      ),
      tags.form(
        Form.selectInput(
          label = "Fractal Type",
          options = Vector(
            "Mandelbrot",
            "JuliaSet",
            "TricornIteration",
            "NewtonIteration"
          ),
          value = fractal.program.getClass.getSimpleName,
          onChange = {
            case "Mandelbrot" => update((lensEdit composeLens FractalEntity.program).set(Mandelbrot())(state))
            case "JuliaSet" => update((lensEdit composeLens FractalEntity.program).set(JuliaSet())(state))
            case "TricornIteration" => update((lensEdit composeLens FractalEntity.program).set(TricornIteration())(state))
            case "NewtonIteration" => update((lensEdit composeLens FractalEntity.program).set(NewtonIteration())(state))
          }
        ),

        seqNode(fractal.program match {
          case f: NewtonIteration =>
            val lensFractal = lensEdit composeLens FractalEntity.program composeLens LenseUtils.lookedUp(f, FractalProgram.newtonIteration.asSetter)
            Seq(
              Form.stringInput("function", lensFractal composeLens NewtonIteration.function),
              Form.stringInput("initial", lensFractal composeLens NewtonIteration.initial),
              Form.intInput("max iterations", lensFractal composeLens NewtonIteration.maxIterations),
              Form.intInput("anti aliase", lensFractal composeLens NewtonIteration.antiAliase),
              Form.doubleInput("threshold", lensFractal composeLens NewtonIteration.threshold),
              Form.doubleInput("brightness factor", lensFractal composeLens NewtonIteration.brightnessFactor),
              Form.tupleDoubleInput("center", lensFractal composeLens NewtonIteration.center),
              Form.doubleInput("overshoot", lensFractal composeLens NewtonIteration.overshoot),
            )
          case _ => Seq.empty
        }),
        Form.stringInput("description", lensEdit composeLens FractalEntity.description),
        Form.stringInput("reference", lensEdit composeLens LenseUtils.withDefault(FractalEntity.reference, "")),
      )
    )
  }

  val dummyTile =
    tags.build("article")(
      attrs.className := "dummy-tile",
      tags.div(
        tags.canvas(
          attrs.widthAttr := 400,
          attrs.heightAttr := 0,
          styles.display := "block",
        )
      )
    )
}
