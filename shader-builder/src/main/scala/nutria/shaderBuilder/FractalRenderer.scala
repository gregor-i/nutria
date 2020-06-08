package nutria.shaderBuilder

import nutria.core.{AntiAliase, FractalImage, Parameter, Viewport}
import nutria.macros.StaticContent
import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.WebGLRenderingContext._
import org.scalajs.dom.raw.{WebGLProgram, WebGLRenderingContext}
import org.scalajs.dom.webgl.Shader

import scala.scalajs.js.Dynamic

object FractalRenderer {

  // todo: result is ignored
  private var cache: (WebGLRenderingContext, FractalImage, WebGLProgram) = null
  def render(canvas: Canvas, image: FractalImage): Either[CompileException, WebGLProgram] = {
    val ctx = canvas
      .getContext("webgl", Dynamic.literal(preserveDrawingBuffer = true))
      .asInstanceOf[WebGLRenderingContext]

    canvas.width = (canvas.clientWidth * dom.window.devicePixelRatio).toInt
    canvas.height = (canvas.clientHeight * dom.window.devicePixelRatio).toInt

    cache match {
      case (`ctx`, cachedImage, cachedProgram) if cachedImage == image =>
        Right(cachedProgram)

      case (`ctx`, cachedImage, cachedProgram) if cachedImage.copy(viewport = image.viewport) == image =>
        render(ctx, image.viewport, cachedProgram)
        Right(cachedProgram)

      case _ =>
        render(image)(ctx).map { program =>
          cache = (ctx, image, program)
          program
        }
    }
  }

  def render(image: FractalImage)(gl: WebGLRenderingContext): Either[CompileException, WebGLProgram] =
    for {
      program <- compileProgram(gl = gl, code = image.template.code, parameters = image.appliedParameters, antiAliase = image.antiAliase)
      _ = render(gl = gl, view = image.viewport, program = program)
    } yield program

  def compileVertexShader(source: String)(gl: WebGLRenderingContext): Either[CompileException, Shader] =
    compileShader(source, VERTEX_SHADER)(gl)

  def compileFragmentShader(source: String)(gl: WebGLRenderingContext): Either[CompileException, Shader] =
    compileShader(source, FRAGMENT_SHADER)(gl)

  def compileShader(source: String, `type`: Int)(gl: WebGLRenderingContext): Either[CompileException, Shader] = {
    val shader = gl.createShader(`type`)
    gl.shaderSource(shader, source)
    gl.compileShader(shader)

    if (!gl.getShaderParameter(shader, COMPILE_STATUS).asInstanceOf[Boolean])
      Left(
        CompileException(
          source = source,
          context = gl,
          shader = shader
        )
      )
    else
      Right(shader)
  }

  def compileProgram(
      gl: WebGLRenderingContext,
      code: String,
      parameters: Vector[Parameter],
      antiAliase: AntiAliase
  ): Either[CompileException, WebGLProgram] =
    for {
      vertexShader   <- compileVertexShader(StaticContent("shader-builder/src/main/glsl/vertex_shader.glsl"))(gl)
      fragmentShader <- compileFragmentShader(FragmentShaderSource(code, parameters, antiAliase))(gl)
    } yield {
      val program = gl.createProgram()
      gl.attachShader(program, vertexShader)
      gl.attachShader(program, fragmentShader)
      gl.linkProgram(program)
      gl.useProgram(program)
      program
    }

  private def render(gl: WebGLRenderingContext, view: Viewport, program: WebGLProgram): Unit = {
    gl.viewport(0, 0, gl.drawingBufferWidth, gl.drawingBufferHeight)

    val buffer = gl.createBuffer()
    gl.bindBuffer(ARRAY_BUFFER, buffer)
    gl.bufferData(
      ARRAY_BUFFER,
      scala.scalajs.js.typedarray.floatArray2Float32Array(
        Array(
          -1.0f, -1.0f, // left  down
          1.0f, -1.0f,  // right down
          -1.0f, 1.0f,  // left  up
          -1.0f, 1.0f,  // left  up
          1.0f, -1.0f,  // right down
          1.0f, 1.0f    // right up
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

}
