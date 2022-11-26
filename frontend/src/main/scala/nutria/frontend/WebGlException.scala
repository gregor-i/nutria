package nutria.frontend

import org.scalajs.dom.{WebGLProgram, WebGLRenderingContext, WebGLShader}

sealed abstract class WebGlException(message: String) extends Exception(message)

case class CompileShaderException(context: WebGLRenderingContext, source: String, shader: WebGLShader)
    extends WebGlException(context.getShaderInfoLog(shader))

case class CompileProgramException(
    context: WebGLRenderingContext,
    program: WebGLProgram,
    vertexShader: WebGLShader,
    fragmentShader: WebGLShader
) extends WebGlException(context.getProgramInfoLog(program))

case class DrawException(context: WebGLRenderingContext) extends WebGlException(s"error code: ${context.getError()}")
