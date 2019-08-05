package nutria.frontend.shaderBuilder

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Positive
import nutria.core.viewport.Viewport
import nutria.core.{FractalEntity, FractalProgram}
import nutria.frontend.util.Untyped
import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.{WebGLProgram, WebGLRenderingContext}

import scala.scalajs.js
import scala.scalajs.js.typedarray.Float32Array
import scala.util.{Failure, Try}

object FractalRenderer {

  def render(canvas: Canvas, entity: FractalEntity, resize: Boolean): Boolean = {
    val viewport = entity.view
    val program = entity.program
    val ctx = canvas.getContext("webgl").asInstanceOf[WebGLRenderingContext]

    if (resize) {
      canvas.width = (canvas.clientWidth * dom.window.devicePixelRatio).toInt
      canvas.height = (canvas.clientHeight * dom.window.devicePixelRatio).toInt
    }

    Try {
      (Untyped(canvas).program, Untyped(canvas).webGlProgram, Untyped(canvas).viewport) match {
        case (cachedProgram, _, cachedViewport) if cachedProgram == Untyped(program.asInstanceOf[js.Object])
          && cachedViewport == Untyped(viewport.asInstanceOf[js.Object]) =>
        // dom.console.log("program and viewport unchanged, skipping render")


        case (cachedProgram, cachedWebGlProgram, _) if cachedProgram == Untyped(program.asInstanceOf[js.Object]) =>
          render(ctx, viewport, cachedWebGlProgram.asInstanceOf[WebGLProgram])
          Untyped(canvas).viewport = viewport.asInstanceOf[js.Object]
        // dom.console.log("program unchanged, rendering with cached webgl program")

        case _ =>
          val (webGlProgram, compileDuration) = messure {
            constructProgram(ctx, program, entity.antiAliase)
          }
          render(ctx, viewport, webGlProgram)
          Untyped(canvas).program = program.asInstanceOf[js.Object]
          Untyped(canvas).webGlProgram = webGlProgram.asInstanceOf[js.Object]
          Untyped(canvas).viewport = viewport.asInstanceOf[js.Object]
        // dom.console.log(s"compile duration: ${compileDuration}ms")
      }
    }.recover{
      case error =>
        dom.console.error(error.getMessage)
        Failure(error)
    }.isSuccess
  }

  def messure[A](op: => A): (A, Long) = {
    val start = System.currentTimeMillis()
    val res = op
    val end = System.currentTimeMillis()
    (res, end - start)
  }

  @throws[Exception]
  def constructProgram(gl: WebGLRenderingContext, program: FractalProgram, antiAliase: Int Refined Positive): WebGLProgram = {
    val vertexShader = gl.createShader(WebGLRenderingContext.VERTEX_SHADER)
    gl.shaderSource(vertexShader, vertexShaderSource)
    gl.compileShader(vertexShader)

    val fragmentShader = gl.createShader(WebGLRenderingContext.FRAGMENT_SHADER)
    gl.shaderSource(fragmentShader, fragmentShaderSource(program, antiAliase))
    gl.compileShader(fragmentShader)

    if (!gl.getShaderParameter(vertexShader, WebGLRenderingContext.COMPILE_STATUS).asInstanceOf[Boolean]) {
      throw new Exception("failed to compile vertex shader:\n" + vertexShaderSource + "\n" + gl.getShaderInfoLog(vertexShader))
    } else if (!gl.getShaderParameter(fragmentShader, WebGLRenderingContext.COMPILE_STATUS).asInstanceOf[Boolean]) {
      throw new Exception("failed to compile fragment shader:\n" + gl.getShaderInfoLog(fragmentShader))
    } else {
      val program = gl.createProgram()
      gl.attachShader(program, vertexShader)
      gl.attachShader(program, fragmentShader)
      gl.linkProgram(program)
      gl.useProgram(program)
      program
    }
  }

  def render(gl: WebGLRenderingContext,
             view: Viewport,
             program: WebGLProgram): Unit = {
    gl.viewport(0, 0, gl.drawingBufferWidth, gl.drawingBufferHeight)

    val buffer = gl.createBuffer()
    gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, buffer)
    gl.bufferData(
      WebGLRenderingContext.ARRAY_BUFFER,
      new Float32Array(scala.scalajs.js.typedarray.floatArray2Float32Array(Array(
        -1.0f, -1.0f, // left  down
        1.0f, -1.0f, // right down
        -1.0f, 1.0f, // left  up

        -1.0f, 1.0f, // left  up
        1.0f, -1.0f, // right down
        1.0f, 1.0f // right up
      ))),
      WebGLRenderingContext.STATIC_DRAW
    )

    val positionLocation = gl.getAttribLocation(program, "a_position")
    gl.enableVertexAttribArray(positionLocation)
    gl.vertexAttribPointer(positionLocation, 2, WebGLRenderingContext.FLOAT, false, 0, 0)

    gl.uniform2f(gl.getUniformLocation(program, "u_resolution"), gl.drawingBufferWidth, gl.drawingBufferHeight)

    val v = view
      .cover(gl.drawingBufferWidth, gl.drawingBufferHeight)
      .flipB
    gl.uniform2f(gl.getUniformLocation(program, "u_view_O"), v.origin._1, v.origin._2)
    gl.uniform2f(gl.getUniformLocation(program, "u_view_A"), v.A._1, v.A._2)
    gl.uniform2f(gl.getUniformLocation(program, "u_view_B"), v.B._1, v.B._2)

    gl.drawArrays(WebGLRenderingContext.TRIANGLES, 0, 6)
  }

  def fragmentShaderSource(state: FractalProgram, antiAliase: Int Refined Positive) = {
    val out = RefVec4("gl_FragColor")

    s"""precision highp float;
       |
       |${GlobalDefinitions.definitions}
       |
       |uniform vec2 u_resolution;
       |uniform vec2 u_view_O, u_view_A, u_view_B;
       |
       |void main() {
       |
       |  ${AntiAliase(FractalProgramToWebGl(state), antiAliase).apply(out)}
       |
       |}
    """.stripMargin
  }


  val vertexShaderSource =
    """attribute vec2 a_position;
      |void main() {
      |  gl_Position = vec4(a_position, 0, 1);
      |}
    """.stripMargin
}
