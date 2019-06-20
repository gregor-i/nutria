package nutria.frontend.shaderBuilder

import nutria.data.FractalProgram
import nutria.frontend.util.Untyped
import org.scalajs.dom
import org.scalajs.dom.CanvasRenderingContext2D
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.WebGLRenderingContext

import scala.scalajs.js.typedarray.Float32Array

object FractalRenderer {

  def render(canvas: Canvas, state: FractalProgram, resize: Boolean): Unit = {
    if (Untyped(dom.window).WebGLRenderingContext != null) {
      if(resize) {
        canvas.width = (canvas.clientWidth * dom.window.devicePixelRatio).toInt
        canvas.height = (canvas.clientHeight * dom.window.devicePixelRatio).toInt
      }
      val ctx = canvas.getContext("webgl").asInstanceOf[WebGLRenderingContext]
      render(ctx, state)
    } else {
      if(resize) {
        canvas.width = canvas.clientWidth
        canvas.height = canvas.clientHeight
      }
      val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
      ctx.font = "30px Arial"
      ctx.textAlign = "center"
      ctx.textBaseline = "middle"
      ctx.fillStyle = "red"
      ctx.fillText("WebGl is not supported", canvas.width / 2, canvas.height / 2)
    }
  }

  def render(gl: WebGLRenderingContext,
             state: FractalProgram): Unit = {
    gl.viewport(0, 0, gl.drawingBufferWidth, gl.drawingBufferHeight)

    var buffer = gl.createBuffer()
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

    val vertexShader = gl.createShader(WebGLRenderingContext.VERTEX_SHADER)
    gl.shaderSource(vertexShader, vertexShaderSource)
    gl.compileShader(vertexShader)

    val fragmentShader = gl.createShader(WebGLRenderingContext.FRAGMENT_SHADER)
    gl.shaderSource(fragmentShader, fragmentShaderSource(state))
    gl.compileShader(fragmentShader)

    if (!gl.getShaderParameter(vertexShader, WebGLRenderingContext.COMPILE_STATUS).asInstanceOf[Boolean]) {
      org.scalajs.dom.console.log("failed to compile vertex shader:")
      org.scalajs.dom.console.log(vertexShaderSource)
      org.scalajs.dom.console.log(gl.getShaderInfoLog(vertexShader))
    } else if (!gl.getShaderParameter(fragmentShader, WebGLRenderingContext.COMPILE_STATUS).asInstanceOf[Boolean]) {
      org.scalajs.dom.console.log("failed to compile fragment shader:")
      org.scalajs.dom.console.log(fragmentShaderSource(state))
      org.scalajs.dom.console.log(gl.getShaderInfoLog(fragmentShader))
    } else {
      val program = gl.createProgram()
      gl.attachShader(program, vertexShader)
      gl.attachShader(program, fragmentShader)
      gl.linkProgram(program)
      gl.useProgram(program)
      var positionLocation = gl.getAttribLocation(program, "a_position")
      gl.enableVertexAttribArray(positionLocation)
      gl.vertexAttribPointer(positionLocation, 2, WebGLRenderingContext.FLOAT, false, 0, 0)

      gl.uniform2f(gl.getUniformLocation(program, "u_resolution"), gl.drawingBufferWidth, gl.drawingBufferHeight)

      val view = state.view.cover(gl.drawingBufferWidth, gl.drawingBufferHeight)

      gl.uniform2f(gl.getUniformLocation(program, "u_view_O"), view.origin._1, view.origin._2)
      gl.uniform2f(gl.getUniformLocation(program, "u_view_A"), view.A._1, view.A._2)
      gl.uniform2f(gl.getUniformLocation(program, "u_view_B"), view.B._1, view.B._2)

      gl.drawArrays(WebGLRenderingContext.TRIANGLES, 0, 6)
    }
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
