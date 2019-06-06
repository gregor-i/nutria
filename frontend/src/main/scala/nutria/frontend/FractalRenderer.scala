package nutria.frontend

import nutria.core.Viewport
import org.scalajs.dom.raw.WebGLRenderingContext

import scala.scalajs.js.typedarray.Float32Array

object FractalRenderer {

  def render(gl: WebGLRenderingContext,
             viewport: Viewport,
             maxIterations: Int): Unit = {
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
    gl.shaderSource(fragmentShader, fragmentShaderSource2(maxIterations, 100.1d*100.1d))
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

    gl.uniform2f(gl.getUniformLocation(program, "u_view_O"), viewport.origin._1, viewport.origin._2)
    gl.uniform2f(gl.getUniformLocation(program, "u_view_A"), viewport.A._1, viewport.A._2)
    gl.uniform2f(gl.getUniformLocation(program, "u_view_B"), viewport.B._1, viewport.B._2)
    gl.uniform1i(gl.getUniformLocation(program, "u_max_iterations"), maxIterations) // todo: why doesnt it work?

    gl.drawArrays(WebGLRenderingContext.TRIANGLES, 0, 6)
  }

  def fragmentShaderSource(maxIterations: Int, escapeRadiusSquared: Double) =
    s"""precision highp float;
       |
       |uniform vec2 u_resolution;
       |uniform vec2 u_view_O, u_view_A, u_view_B;
       |uniform int u_max_iterations;
       |
       |void main() {
       |
       |  vec2 pos = gl_FragCoord.xy / u_resolution;
       |  vec2 p = u_view_O + pos.x * u_view_A + pos.y * u_view_B;
       |
       |  int l = 0;
       |  vec2 c = p;
       |  vec2 z = vec2(0.0, 0.0);
       |  int max = u_max_iterations;
       |  for(int i = 0;i< $maxIterations; i++){
       |		z = vec2(z.x*z.x - z.y*z.y + c.x, z.x*z.y * 2.0 + c.y);
       |    if(dot(z,z) > $escapeRadiusSquared)
       |      break;
       |    l ++;
       |  }
       |
       |  float fract = float(l) / float(u_max_iterations);
       |  gl_FragColor = vec4(fract, fract, fract,1.0);
       |  //gl_FragColor = vec4(hsb2rgb(vec3(fract, 1, 1)), 1.0);
       |
       |}
    """.stripMargin

  def fragmentShaderSource2(maxIterations: Int, escapeRadiusSquared: Double) =
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
       |uniform int u_max_iterations;
       |
       |float h2 = 1.5;  // height factor of the incoming light
       |float angle = ${45.0 / 180.0 * Math.PI};  // incoming direction of light
       |vec2 v = vec2(sin(angle), cos(angle));  // unit 2D vector in this direction
       |// incoming light 3D vector = (v.re,v.im,h2)
       |
       |int R = 100; // # do not take R too small
       |
       |void main() {
       |
       |  vec2 pos = gl_FragCoord.xy / u_resolution;
       |  vec2 p = u_view_O + pos.x * u_view_A + pos.y * u_view_B;
       |
       |  int l = 0;
       |  vec2 c = p;
       |  vec2 z = c;
       |  vec2 der1 = vec2(1.0, 0.0);
       |  int max = u_max_iterations;
       |  for(int i = 0;i< $maxIterations; i++){
       |	  vec2 new_z = product(z,z) + c;
       |    vec2 new_der1 = product(der1, z) * 2.0 + vec2(1.0, 0.0);
       |    z = new_z;
       |    der1 = new_der1;
       |    if(dot(z,z) > $escapeRadiusSquared)
       |      break;
       |    l ++;
       |  }
       |
       |  float fract = float(l) / float(u_max_iterations);
       |
       |  if(l == u_max_iterations){
       |    gl_FragColor = vec4(0.0, 0.0, 0.5, 1.0);
       |  }else{
       |    vec2 u = divide(z, der1);
       |    float absu = sqrt(u.x*u.x+u.y*u.y);
       |    u = u/absu;
       |    float t = u.x*v.x + u.y*v.y + h2; //  # dot product with the incoming light
       |    t = t/(1.0+h2); //  # rescale so that t does not get bigger than 1
       |    if(t<0.0) {
       |      t = 0.0;
       |    }
       |    gl_FragColor = vec4(mix(vec3(0.0, 0.0, 0.0), vec3(1.0, 1.0, 1.0), t),1.0);
       |  }
       |
       |}
    """.stripMargin

  val vertexShaderSource =
    """attribute vec2 a_position;
      |void main() {
      |  gl_Position = vec4(a_position, 0, 1);
      |}
    """.stripMargin
}
