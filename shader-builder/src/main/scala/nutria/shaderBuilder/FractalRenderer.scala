package nutria.shaderBuilder

import nutria.core.{AntiAliase, FractalImage, FractalTemplate, Parameter, Viewport}
import nutria.macros.StaticContent
import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.webgl.Shader
import org.scalajs.dom.webgl.Program
import org.scalajs.dom.webgl.RenderingContext
import org.scalajs.dom.raw.WebGLRenderingContext._

import scala.scalajs.js
import scala.scalajs.js.Dynamic

object FractalRenderer {

  // todo: result is ignored
  private var cache: (RenderingContext, FractalImage, Program) = null
  def render(canvas: Canvas, image: FractalImage): Either[WebGlException, Program] = {
    val ctx = canvas
      .getContext("webgl", Dynamic.literal(preserveDrawingBuffer = true))
      .asInstanceOf[RenderingContext]

    canvas.width = (canvas.clientWidth * dom.window.devicePixelRatio).toInt
    canvas.height = (canvas.clientHeight * dom.window.devicePixelRatio).toInt

    cache match {
      case (`ctx`, cachedImage, cachedProgram) if cachedImage == image =>
        Right(cachedProgram)

      case (`ctx`, cachedImage, cachedProgram) if cachedImage.copy(viewport = image.viewport) == image =>
        draw(image.viewport, cachedProgram)(ctx)
        Right(cachedProgram)

      case _ =>
        render(image)(ctx).map { program =>
          cache = (ctx, image, program)
          program
        }
    }
  }

  def validateSource(fractalTemplate: FractalTemplate)(gl: RenderingContext): Either[CompileShaderException, Shader] =
    compileFragmentShader(FragmentShaderSource(fractalTemplate.code, fractalTemplate.parameters, 1))(gl)

  def render(image: FractalImage)(gl: RenderingContext): Either[WebGlException, Program] =
    for {
      vertexShader   <- compileVertexShader(StaticContent("shader-builder/src/main/glsl/vertex_shader.glsl"))(gl)
      fragmentShader <- compileFragmentShader(FragmentShaderSource(image.template.code, image.appliedParameters, image.antiAliase))(gl)
      program        <- linkProgram(vertexShader, fragmentShader)(gl)
      _              <- draw(image.viewport, program)(gl)
    } yield program

  private def compileVertexShader(source: String)(gl: RenderingContext): Either[CompileShaderException, Shader] =
    compileShader(source, VERTEX_SHADER)(gl)

  private def compileFragmentShader(source: String)(gl: RenderingContext): Either[CompileShaderException, Shader] =
    compileShader(source, FRAGMENT_SHADER)(gl)

  private def compileShader(source: String, `type`: Int)(gl: RenderingContext): Either[CompileShaderException, Shader] = {
    val shader = gl.createShader(`type`)
    gl.shaderSource(shader, source)
    gl.compileShader(shader)

    if (!gl.getShaderParameter(shader, COMPILE_STATUS).asInstanceOf[Boolean])
      Left(
        CompileShaderException(
          source = source,
          context = gl,
          shader = shader
        )
      )
    else
      Right(shader)
  }

  private def linkProgram(vertexShader: Shader, fragmentShader: Shader)(
      gl: RenderingContext
  ): Either[CompileProgramException, Program] = {
    val program = gl.createProgram()
    gl.attachShader(program, vertexShader)
    gl.attachShader(program, fragmentShader)
    gl.linkProgram(program)

    if (gl.getProgramParameter(program, RenderingContext.LINK_STATUS).asInstanceOf[Boolean])
      Right(program)
    else
      Left(CompileProgramException(gl, program, vertexShader, fragmentShader))
  }

  private val triangles = scala.scalajs.js.typedarray.floatArray2Float32Array(
    Array(
      -1.0f, -1.0f, // left  down
      1.0f, -1.0f,  // right down
      -1.0f, 1.0f,  // left  up
      -1.0f, 1.0f,  // left  up
      1.0f, -1.0f,  // right down
      1.0f, 1.0f    // right up
    )
  )

  private def draw(view: Viewport, program: Program)(gl: RenderingContext): Either[DrawException, Unit] = {
    gl.useProgram(program)
    gl.viewport(0, 0, gl.drawingBufferWidth, gl.drawingBufferHeight)

    val buffer = gl.createBuffer()
    gl.bindBuffer(ARRAY_BUFFER, buffer)
    gl.bufferData(ARRAY_BUFFER, triangles, STATIC_DRAW)

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

    if (gl.getError() == NO_ERROR)
      Right(())
    else
      Left(DrawException(gl))
  }

}
