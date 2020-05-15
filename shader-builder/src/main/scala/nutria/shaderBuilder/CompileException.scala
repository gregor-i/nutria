package nutria.shaderBuilder

import org.scalajs.dom.raw.{WebGLRenderingContext, WebGLShader}

case class CompileException(context: WebGLRenderingContext, source: String, shader: WebGLShader) extends Exception
