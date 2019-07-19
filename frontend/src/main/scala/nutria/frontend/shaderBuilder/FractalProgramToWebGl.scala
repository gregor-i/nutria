package nutria.frontend.shaderBuilder

import mathParser.implicits._
import nutria.core._

object FractalProgramToWebGl {
  def apply(fractalProgram: FractalProgram): RefVec4 => String =
    fractalProgram match {
      case f: NewtonIteration => AntiAliase(newtonIteration(f), f.antiAliase)
      case f: DivergingSeries => AntiAliase(divergingSeries(f), f.antiAliase)
      case f: DerivedDivergingSeries => AntiAliase(derivedDivergingSeries(f), f.antiAliase)
    }

  def derivedDivergingSeries(f: DerivedDivergingSeries)
                            (inputVar: RefVec2, outputVar: RefVec4) = {
    import nutria.core.derivedDivergingSeries._

    val iterationZ = Language.iterationZ.optimize(Language.iterationZ.parse(f.iterationZ).get)
    val iterationZDer = Language.iterationZDer.optimize(Language.iterationZDer.parse(f.iterationZDer).get)

    val initalZ = Language.initial.optimize(Language.initial.parse(f.initialZ).get)
    val initalZDer = Language.initial.optimize(Language.initial.parse(f.initialZDer).get)

    val z = RefVec2("z")
    val zNew = RefVec2("z_new")
    val zDer = RefVec2("z_der")
    val zDerNew = RefVec2("z_der_new")

    val iterationLangNames: PartialFunction[ZAndLambda, String] = {
      case Z => z.name
      case Lambda => inputVar.name
    }

    val iterationDerLangNames: PartialFunction[ZAndZDerAndLambda, String] = {
      case Z => z.name
      case ZDer => zDer.name
      case Lambda => inputVar.name
    }

    val initialLangNames: PartialFunction[Lambda.type, String] = {
      case Lambda => inputVar.name
    }

    s"""{
       |  int l = 0;
       |  ${WebGlType.declare(z, PureStringExpression(NewtonLang.toWebGlCode(initalZ, initialLangNames)))}
       |  ${WebGlType.declare(zDer, PureStringExpression(NewtonLang.toWebGlCode(initalZDer, initialLangNames)))}
       |  for(int i = 0; i < ${f.maxIterations}; i++){
       |    ${WebGlType.declare(zNew, PureStringExpression(NewtonLang.toWebGlCode(iterationZ, iterationLangNames)))}
       |    ${WebGlType.declare(zDerNew, PureStringExpression(NewtonLang.toWebGlCode(iterationZDer, iterationDerLangNames)))}
       |    ${WebGlType.assign(z, RefExp(zNew))}
       |    ${WebGlType.assign(zDer, RefExp(zDerNew))}
       |    if(dot(z,z) > float(${f.escapeRadius * f.escapeRadius}))
       |      break;
       |    l ++;
       |  }
       |
       |  if(l == ${f.maxIterations}){
       |    ${outputVar.name} = vec4(0.0, 0.0, 0.25, 1.0);
       |  }else{
       |    const float h2 = float(${f.h2});
       |    const vec2 v = vec2(float(${Math.cos(f.angle)}), float(${Math.sin(f.angle)}));
       |    vec2 u = normalize(complex_divide(${z.name}, ${zDer.name}));
       |    float t = max((dot(u, v) + h2) / (1.0 + h2), 0.0);
       |    ${outputVar.name} = mix(vec4(0.0, 0.0, 0.0, 1.0), vec4(1.0, 1.0, 1.0, 1.0), t);
       |  }
       |}
    """.stripMargin
  }


  def newtonIteration(n: NewtonIteration)(inputVar: RefVec2, outputVar: RefVec4): String = {
    import nutria.core.newton._
    val iteration = NewtonLang.functionLang.optimize(NewtonLang.functionLang.parse(n.function).getOrElse(throw new Exception(n.function)))
    val derived = NewtonLang.functionLang.optimize(NewtonLang.functionLang.derive(iteration)(X))

    val initial = NewtonLang.initialLang.optimize(NewtonLang.initialLang.parse(n.initial).getOrElse(throw new Exception(n.initial)))

    val z = RefVec2("z")
    val fzlast = RefVec2("fzlast")
    val fz = RefVec2("fz")
    val fderz = RefVec2("fderz")

    val functionLangNames: PartialFunction[XAndLambda, String] = {
      case X => "z"
      case Lambda => "p"
    }

    val initialLangNames: PartialFunction[Lambda.type, String] = {
      case Lambda => "p"
    }

    s"""{
       |  int l = 0;
       |  ${WebGlType.declare(z, PureStringExpression(NewtonLang.toWebGlCode(initial, initialLangNames)))}
       |  ${WebGlType.declare(fz, PureStringExpression(NewtonLang.toWebGlCode(iteration, functionLangNames)))}
       |  ${WebGlType.declare(fzlast, RefExp(fz))}
       |  for(int i = 0;i< ${n.maxIterations}; i++){
       |    ${WebGlType.assign(fzlast, RefExp(fz))}
       |    ${WebGlType.assign(fz, PureStringExpression(NewtonLang.toWebGlCode(iteration, functionLangNames)))}
       |    ${WebGlType.declare(fderz, PureStringExpression(NewtonLang.toWebGlCode(derived, functionLangNames)))}
       |    ${z.name} -= ${FloatLiteral(n.overshoot.toFloat).toCode} * complex_divide(${fz.name}, ${fderz.name});
       |    if(length(${fz.name}) < ${FloatLiteral(n.threshold.toFloat).toCode})
       |      break;
       |    l ++;
       |  }
       |
       |  float fract = 0.0;
       |  if(fz == ${WebGlType.zero[WebGlTypeVec2.type].toCode}){
       |    fract = float(l - 1);
       |  }else{
       |    fract = float(l) - log(${n.threshold} / length(${fz.name})) / log( length(${fzlast.name}) / length(${fz.name}));
       |  }
       |
       |  float H = atan(z.x - ${FloatLiteral(n.center._1.toFloat).toCode}, z.y - ${FloatLiteral(n.center._2.toFloat).toCode}) / float(${2 * Math.PI});
       |  float V = exp(-fract / ${FloatLiteral(n.brightnessFactor.toFloat).toCode});
       |  float S = length(z);
       |
       |  ${outputVar.name} = vec4(hsv2rgb(vec3(H, S, V)), 1.0);
       |}
       """.stripMargin
  }


  def divergingSeries(n: DivergingSeries)(inputVar: RefVec2, outputVar: RefVec4): String = {
    import nutria.core.divergingSeries._
    val inital = DivergingSeriesLang.initialLang.optimize(DivergingSeriesLang.initialLang.parse(n.initial).getOrElse(throw new Exception(n.initial)))
    val iteration = DivergingSeriesLang.functionLang.optimize(DivergingSeriesLang.functionLang.parse(n.iteration).getOrElse(throw new Exception(n.iteration)))

    val functionLangNames: PartialFunction[ZAndLambda, String] = {
      case Z => "z"
      case Lambda => "p"
    }

    val initialLangNames: PartialFunction[Lambda.type, String] = {
      case Lambda => "p"
    }

    val z = RefVec2("z")
    s"""{
       |  int l = 0;
       |  ${WebGlType.declare(z, PureStringExpression(DivergingSeriesLang.toWebGlCode(inital, initialLangNames)))}
       |  for(int i = 0;i< ${n.maxIterations}; i++){
       |    ${WebGlType.assign(z, PureStringExpression(DivergingSeriesLang.toWebGlCode(iteration, functionLangNames)))}
       |    if(length(${z.name}) > ${FloatLiteral(n.escapeRadius.toFloat).toCode})
       |      break;
       |    l ++;
       |  }
       |
       |  float fract = float(l) / float(${n.maxIterations});
       |  ${outputVar.name} = vec4(fract, fract, fract, 1.0);
       |}
       """.stripMargin
  }

}
