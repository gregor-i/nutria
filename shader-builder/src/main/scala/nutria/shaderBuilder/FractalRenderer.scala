package nutria.shaderBuilder

import nutria.core.viewport.Viewport
import nutria.core.{AntiAliase, FractalImage, FractalProgram}
import nutria.macros.StaticContent
import nutria.shaderBuilder.templates.MainTemplate
import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.WebGLRenderingContext._
import org.scalajs.dom.raw.{WebGLProgram, WebGLRenderingContext}

import scala.scalajs.js
import scala.scalajs.js.Dynamic
import scala.scalajs.js.typedarray.Float32Array
import scala.util.{Failure, Try}

object FractalRenderer {

  def render(canvas: Canvas, entity: FractalImage, resize: Boolean): Boolean = {
    val viewport = entity.view
    val program  = entity.program
    val ctx = canvas
      .getContext("webgl", Dynamic.literal(preserveDrawingBuffer = true))
      .asInstanceOf[WebGLRenderingContext]

    if (resize) {
      canvas.width = (canvas.clientWidth * dom.window.devicePixelRatio).toInt
      canvas.height = (canvas.clientHeight * dom.window.devicePixelRatio).toInt
    }

    Try {
      (Untyped(canvas).program, Untyped(canvas).webGlProgram, Untyped(canvas).viewport) match {
        case (cachedProgram, _, cachedViewport)
            if cachedProgram == Untyped(program.asInstanceOf[js.Object])
              && cachedViewport == Untyped(viewport.asInstanceOf[js.Object]) =>
        case (cachedProgram, cachedWebGlProgram, _) if cachedProgram == Untyped(program.asInstanceOf[js.Object]) =>
          render(ctx, viewport, cachedWebGlProgram.asInstanceOf[WebGLProgram])
          Untyped(canvas).viewport = viewport.asInstanceOf[js.Object]

        case _ =>
          val webGlProgram = constructProgram(ctx, program, entity.antiAliase)
          render(ctx, viewport, webGlProgram)
          Untyped(canvas).program = program.asInstanceOf[js.Object]
          Untyped(canvas).webGlProgram = webGlProgram.asInstanceOf[js.Object]
          Untyped(canvas).viewport = viewport.asInstanceOf[js.Object]
      }
    }.recover {
      case error =>
        dom.console.error(error.getMessage)
        Failure(error)
    }.isSuccess
  }

  @throws[Exception]
  def constructProgram(
      gl: WebGLRenderingContext,
      fractralProgram: FractalProgram,
      antiAliase: AntiAliase
  ): WebGLProgram = {
    val vertexShader = gl.createShader(VERTEX_SHADER)
    gl.shaderSource(vertexShader, StaticContent("shader-builder/src/main/glsl/vertex_shader.glsl"))
    gl.compileShader(vertexShader)

    val fragmentShader = gl.createShader(FRAGMENT_SHADER)
    gl.shaderSource(fragmentShader, fragmentShaderSource(fractralProgram, antiAliase))
    gl.compileShader(fragmentShader)

    if (!gl
          .getShaderParameter(vertexShader, COMPILE_STATUS)
          .asInstanceOf[Boolean]) {
      throw new Exception(s"""failed to compile vertex shader:
           |${gl.getShaderInfoLog(vertexShader)}""".stripMargin)
    } else if (!gl
                 .getShaderParameter(fragmentShader, COMPILE_STATUS)
                 .asInstanceOf[Boolean]) {
      throw new Exception(
        s"""failed to compile fragment shader:
           |${fragmentShaderSource(fractralProgram, antiAliase)}
           |${gl.getShaderInfoLog(fragmentShader)}""".stripMargin
      )
    } else {
      val program = gl.createProgram()
      gl.attachShader(program, vertexShader)
      gl.attachShader(program, fragmentShader)
      gl.linkProgram(program)
      gl.useProgram(program)
      program
    }
  }

  def render(gl: WebGLRenderingContext, view: Viewport, program: WebGLProgram): Unit = {
    gl.viewport(0, 0, gl.drawingBufferWidth, gl.drawingBufferHeight)

    val buffer = gl.createBuffer()
    gl.bindBuffer(ARRAY_BUFFER, buffer)
    gl.bufferData(
      ARRAY_BUFFER,
      new Float32Array(
        scala.scalajs.js.typedarray.floatArray2Float32Array(
          Array(
            -1.0f, -1.0f, // left  down
            1.0f, -1.0f,  // right down
            -1.0f, 1.0f,  // left  up
            -1.0f, 1.0f,  // left  up
            1.0f, -1.0f,  // right down
            1.0f, 1.0f    // right up
          )
        )
      ),
      STATIC_DRAW
    )

    val positionLocation = gl.getAttribLocation(program, "a_position")
    gl.enableVertexAttribArray(positionLocation)
    gl.vertexAttribPointer(positionLocation, 2, FLOAT, false, 0, 0)

    gl.uniform2f(
      gl.getUniformLocation(program, "u_resolution"),
      gl.drawingBufferWidth,
      gl.drawingBufferHeight
    )

    val v = view.cover(gl.drawingBufferWidth, gl.drawingBufferHeight).flipB
    gl.uniform2f(gl.getUniformLocation(program, "u_view_O"), v.origin._1, v.origin._2)
    gl.uniform2f(gl.getUniformLocation(program, "u_view_A"), v.A._1, v.A._2)
    gl.uniform2f(gl.getUniformLocation(program, "u_view_B"), v.B._1, v.B._2)

    gl.drawArrays(TRIANGLES, 0, 6)
  }

  def fragmentShaderSource(state: FractalProgram, antiAliase: AntiAliase) = {
    s"""precision highp float;
       |
       |uniform vec2 u_resolution;
       |uniform vec2 u_view_O, u_view_A, u_view_B;
       |
       |${StaticContent("shader-builder/src/main/glsl/global_definitions.glsl")}
       |
       |${MainTemplate.definitions(state).mkString("\n")}
       |
       |vec4 main_template(vec2 p) {
       |  vec4 result;
       |  ${MainTemplate.main(state)(RefVec2("p"), RefVec4("result"))}
       |  return result;
       |}
       |
       |void main() {
       |
       |${AntiAliase(antiAliase).apply(RefVec4("gl_FragColor"))}
       |
       |}
    """.stripMargin
  }
}
