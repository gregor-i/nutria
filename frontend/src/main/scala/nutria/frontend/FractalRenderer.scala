package nutria.frontend

import nutria.frontend.shaderBuilder._
import org.scalajs.dom.raw.WebGLRenderingContext

import scala.scalajs.js.typedarray.Float32Array

object FractalRenderer {

  def render(gl: WebGLRenderingContext,
             state: State): Unit = {
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

    var vertexShader = gl.createShader(WebGLRenderingContext.VERTEX_SHADER)
    gl.shaderSource(vertexShader, vertexShaderSource)
    gl.compileShader(vertexShader)

    var fragmentShader = gl.createShader(WebGLRenderingContext.FRAGMENT_SHADER)
    gl.shaderSource(fragmentShader, fragmentShaderSource(state))
    gl.compileShader(fragmentShader)

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

    org.scalajs.dom.console.log(fragmentShaderSource(state))

    gl.drawArrays(WebGLRenderingContext.TRIANGLES, 0, 6)
  }

  def fragmentShaderSource(state: State) = {
    val block: RefVec4 => String = AntiAliase(
      if (state.shaded) Consumer.shaded(state.maxIterations, state.escapeRadius, state.iteration)
      else Consumer.iterations(state.maxIterations, state.escapeRadius, state.iteration),
      state.antiAliase)

    s"""precision highp float;
       |
       |//Define complex operations
       |#define product(a, b) vec2(a.x*b.x-a.y*b.y, a.x*b.y+a.y*b.x)
       |#define conjugate(a) vec2(a.x,-a.y)
       |#define divide(a, b) vec2(((a.x*b.x+a.y*b.y)/(b.x*b.x+b.y*b.y)),((a.y*b.x-a.x*b.y)/(b.x*b.x+b.y*b.y)))
       |
       |
       |uniform vec2 u_resolution;
       |uniform vec2 u_view_O, u_view_A, u_view_B;
       |
       |void main() {
       |
       |  ${block(RefVec4("gl_FragColor"))}
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
