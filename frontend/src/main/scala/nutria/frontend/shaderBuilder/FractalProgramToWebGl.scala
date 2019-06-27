package nutria.frontend.shaderBuilder

import mathParser.complex.ComplexLanguage
import nutria.data._

object FractalProgramToWebGl {
  def apply(fractalProgram: FractalProgram): RefVec4 => String =
    fractalProgram match {
      case f: Mandelbrot if f.shaded => AntiAliase(shaded(f.maxIterations, f.escapeRadius)(deriveableInitial, deriveableInitialStepMandelbrot), f.antiAliase)
      case f: Mandelbrot => AntiAliase(countIterations(f.maxIterations, f.escapeRadius)(initial, stepMandelbrot), f.antiAliase)
      case f: JuliaSet if f.shaded => AntiAliase(shaded(f.maxIterations, f.escapeRadius)(deriveableInitial, deriveableInitialStepJuliaset(f.c)), f.antiAliase)
      case f: JuliaSet => AntiAliase(countIterations(f.maxIterations, f.escapeRadius)(initial, stepJulia(f.c)), f.antiAliase)
      case f: TricornIteration => AntiAliase(countIterations(f.maxIterations, f.escapeRadius)(initial, stepTricorn), f.antiAliase)
      case f: NewtonIteration => AntiAliase(newtonIteration(f.maxIterations, f.threshold, f.function), f.antiAliase)
    }

  def initial(z: RefVec2, p: RefVec2): String =
    s"""
       |vec2 ${z.name} = ${p.name};
       |""".stripMargin

  def stepMandelbrot(z: RefVec2, p: RefVec2): String =
    s"""
       |${z.name} = product(${z.name}, ${z.name}) + ${p.name};
       |""".stripMargin

  def stepJulia(c: (Double, Double))
               (z: RefVec2, p: RefVec2): String =
    s"""
       |${z.name} = product(${z.name}, ${z.name}) + ${Vec2(FloatLiteral(c._1.toFloat), FloatLiteral(c._2.toFloat)).toCode};
       |""".stripMargin

  def stepTricorn(z: RefVec2, p: RefVec2): String =
    s"""
       |${z.name} = conjugate(product(${z.name}, ${z.name})) + ${p.name};
       |""".stripMargin


  def deriveableInitial(z: RefVec2, p: RefVec2): String =
    s"""
       |${WebGlType.declare(z, RefExp(p))};
       |vec2 ${z.name}_der = vec2(1.0, 0.0);
       """.stripMargin

  def deriveableInitialStepMandelbrot(z: RefVec2, p: RefVec2): String =
    s"""
       |vec2 ${z.name}_new = product(${z.name}, ${z.name}) + ${p.name};
       |vec2 ${z.name}_der_new = product(${z.name}_der, z) * 2.0 + vec2(1.0, 0.0);
       |${z.name} = ${z.name}_new;
       |${z.name}_der = ${z.name}_der_new;
       |""".stripMargin


  def deriveableInitialStepJuliaset(c: (Double, Double))(z: RefVec2, p: RefVec2): String =
    s"""
       |vec2 ${z.name}_new = product(${z.name}, ${z.name}) + vec2(float(${c._1}), float(${c._2}));
       |vec2 ${z.name}_der_new = product(${z.name}_der, z) * 2.0 + vec2(1.0, 0.0);
       |${z.name} = ${z.name}_new;
       |${z.name}_der = ${z.name}_der_new;
       |""".stripMargin

  def shaded(maxIterations: Int, escapeRadiusSquared: Double)
            (init: (RefVec2, RefVec2) => String, step: (RefVec2, RefVec2) => String)
            (inputVar: RefVec2, outputVar: RefVec4) = {
    val h2 = 2.0
    val angle = 45.0 / 180.0 * Math.PI
    val vx = Math.sin(angle)
    val vy = Math.sin(angle)
    // incoming light 3D vector = (v.re,v.im,h2)
    s"""{
       |  int l = 0;
       |	${init(RefVec2("z"), inputVar)}
       |  for(int i = 0; i < $maxIterations; i++){
       |	  ${step(RefVec2("z"), inputVar)}
       |    if(dot(z,z) > float($escapeRadiusSquared))
       |      break;
       |    l ++;
       |  }
       |
       |  if(l == $maxIterations){
       |    ${outputVar.name} = vec4(0.0, 0.0, 0.25, 1.0);
       |  }else{
       |    const float h2 = float($h2);
       |    const vec2 v = vec2(float($vx), float($vy));
       |    vec2 u = normalize(divide(z, z_der));
       |    float t = max((dot(u, v) + h2) / (1.0 + h2), 0.0);
       |    ${outputVar.name} = mix(vec4(0.0, 0.0, 0.0, 1.0), vec4(1.0, 1.0, 1.0, 1.0), t);
       |  }
       |}
       """.stripMargin
  }

  def countIterations(maxIterations: Int, escapeRadiusSquared: Double)
                     (init: (RefVec2, RefVec2) => String, step: (RefVec2, RefVec2) => String)
                     (inputVar: RefVec2, outputVar: RefVec4): String =
    s"""{
       |  int l = 0;
       |  ${init(RefVec2("z"), inputVar)}
       |  for(int i = 0;i< $maxIterations; i++){
       |		${step(RefVec2("z"), inputVar)}
       |    if(dot(z,z) > float($escapeRadiusSquared))
       |      break;
       |    l ++;
       |  }
       |
       |  float fract = float(l) / float($maxIterations);
       |  ${outputVar.name} = vec4(fract, fract, fract,1.0);
       |}
       """.stripMargin


  def newtonIteration(maxIterations: Int, threshold: Double, fn: String)(inputVar: RefVec2, outputVar: RefVec4): String = {
    val node = Parser.lang.parse(fn).get
    val derived = Parser.lang.derive(node)('x)

    val z = RefVec2("z")
    val fzlast = RefVec2("fzlast")
    val fz = RefVec2("fz")
    val fderz = RefVec2("fderz")

    s"""{
       |  int l = 0;
       |  ${WebGlType.declare(z, RefExp(inputVar))}
       |  ${WebGlType.declare(fz, PureStringExpression(toCode(node)))}
       |  ${WebGlType.declare(fzlast, RefExp(fz))}
       |  for(int i = 0;i< $maxIterations; i++){
       |    ${WebGlType.assign(fzlast, RefExp(fz))}
       |    ${WebGlType.assign(fz, PureStringExpression(toCode(node)))};
       |    ${WebGlType.declare(fderz, PureStringExpression(toCode(derived)))}
       |    ${z.name} -= divide(${fz.name}, ${fderz.name});
       |    if(length(${fz.name}) < ${FloatLiteral(threshold.toFloat).toCode})
       |      break;
       |    l ++;
       |  }
       |
       |  float fract = 0.0;
       |  if(fz == ${WebGlType.zero[WebGlTypeVec2.type].toCode}){
       |    fract = float(l - 1);
       |  }else{
       |    fract = float(l) - log(${threshold} / length(${fz.name})) / log( length(${fzlast.name}) / length(${fz.name}));
       |  }
       |
       |  float H = atan(z.x, z.y) / float(${2 * Math.PI}) + 0.5;
       |  float S = exp(-fract / 25.0);
       |  float V = S;
       |
       |  ${outputVar.name} = vec4(hsv2rgb(vec3(H, S, V)), 1.0);
       |}
       """.stripMargin
  }


  def toCode(node: ComplexLanguage#Node): String =
    node.fold[String](
      ifConstant = c => Vec2(FloatLiteral(c.real.toFloat), FloatLiteral(c.imag.toFloat)).toCode,
      ifBinary = (op, left, right) => op match {
        case Parser.lang.Plus => left + "+" + right
        case Parser.lang.Minus => left + "-" + right
        case Parser.lang.Times => s"product(vec2($left), vec2($right))"
        case Parser.lang.Divided => s"divide(vec2($left), vec2($right))"
        case Parser.lang.Power =>
          println("power")
          ???
      },
      ifUnitary = (op, child) => ???,
      ifVariable = _ match {
        case 'x => "z"
      }
    )
}