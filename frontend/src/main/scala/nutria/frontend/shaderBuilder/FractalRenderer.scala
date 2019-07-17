package nutria.frontend.shaderBuilder

import nutria.core.FractalProgram
import nutria.core.viewport.Viewport
import nutria.frontend.util.Untyped
import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.{WebGLProgram, WebGLRenderingContext}

import scala.scalajs.js
import scala.scalajs.js.typedarray.Float32Array
import scala.util.Try

object FractalRenderer {

  def render(canvas: Canvas, state: FractalProgram, resize: Boolean): Boolean = {
    Try {
      if (Untyped(canvas).state != Untyped(state.asInstanceOf[js.Object])) {
        val ctx = canvas.getContext("webgl").asInstanceOf[WebGLRenderingContext]
        val (program, compileDuration) = messure {
          Untyped(canvas).program match {
            case program: WebGLProgram => program
            case _ => constructProgram(ctx, state)
          }
        }
        if (resize) {
          canvas.width = (canvas.clientWidth * dom.window.devicePixelRatio).toInt
          canvas.height = (canvas.clientHeight * dom.window.devicePixelRatio).toInt
        }
        render(ctx, state.view, program)
        Untyped(canvas).state = state.asInstanceOf[js.Object]
        Untyped(canvas).program = program
        dom.console.log(s"compile duration: ${compileDuration}ms")
      } else {
        dom.console.log("state unchanged, skipping render")
      }
    }.isSuccess
  }

  def messure[A](op: => A): (A, Long) = {
    val start = System.currentTimeMillis()
    val res = op
    val end = System.currentTimeMillis()
    (res, end - start)
  }

  @throws[Exception]
  def constructProgram(gl: WebGLRenderingContext, program: FractalProgram): WebGLProgram = {
    val vertexShader = gl.createShader(WebGLRenderingContext.VERTEX_SHADER)
    gl.shaderSource(vertexShader, vertexShaderSource)
    gl.compileShader(vertexShader)

    val fragmentShader = gl.createShader(WebGLRenderingContext.FRAGMENT_SHADER)
    gl.shaderSource(fragmentShader, fragmentShaderSource(program))
    gl.compileShader(fragmentShader)

    if (!gl.getShaderParameter(vertexShader, WebGLRenderingContext.COMPILE_STATUS).asInstanceOf[Boolean]) {
      throw new Exception("failed to compile vertex shader:\n" + gl.getShaderInfoLog(vertexShader))
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

    val v = view.cover(gl.drawingBufferWidth, gl.drawingBufferHeight)
    gl.uniform2f(gl.getUniformLocation(program, "u_view_O"), v.origin._1, v.origin._2)
    gl.uniform2f(gl.getUniformLocation(program, "u_view_A"), v.A._1, v.A._2)
    gl.uniform2f(gl.getUniformLocation(program, "u_view_B"), v.B._1, v.B._2)

    gl.drawArrays(WebGLRenderingContext.TRIANGLES, 0, 6)
  }

  def fragmentShaderSource(state: FractalProgram) = {
    val out = RefVec4("gl_FragColor")

    s"""precision highp float;
       |
       |${GlobalDefintions.definitions}
       |
       |uniform vec2 u_resolution;
       |uniform vec2 u_view_O, u_view_A, u_view_B;
       |
       |
       |vec3 hsv2rgb(vec3 c)
       |{
       |    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
       |    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
       |    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
       |}
       |
       |
       |void main() {
       |
       |  ${FractalProgramToWebGl(state)(out)}
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
