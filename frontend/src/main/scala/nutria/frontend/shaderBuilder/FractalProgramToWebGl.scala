package nutria.frontend.shaderBuilder

import mathParser.algebra._
import mathParser.implicits._
import mathParser.{BinaryNode, ConstantNode, Optimizer}
import nutria.core._
import spire.math.Complex

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

    val iterationLangNames: PartialFunction[ZAndLambda, RefExp[WebGlTypeVec2.type]] = {
      case Z => RefExp(z)
      case Lambda => RefExp(inputVar)
    }

    val iterationDerLangNames: PartialFunction[ZAndZDerAndLambda, RefExp[WebGlTypeVec2.type]] = {
      case Z => RefExp(z)
      case ZDer => RefExp(zDer)
      case Lambda => RefExp(inputVar)
    }

    val initialLangNames: PartialFunction[Lambda.type, RefExp[WebGlTypeVec2.type]] = {
      case Lambda => RefExp(inputVar)
    }

    s"""{
       |  int l = 0;
       |  ${WebGlType.declare(z, WebGlExpression.toExpression(initalZ, initialLangNames))}
       |  ${WebGlType.declare(zDer, WebGlExpression.toExpression(initalZDer, initialLangNames))}
       |  for(int i = 0; i < ${f.maxIterations}; i++){
       |    ${WebGlType.declare(zNew, WebGlExpression.toExpression(iterationZ, iterationLangNames))}
       |    ${WebGlType.declare(zDerNew, WebGlExpression.toExpression(iterationZDer, iterationDerLangNames))}
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
    val iteration =
      Language.fLang.optimize(
        Language.fLang.optimize(Language.fLang.parse(n.function).get)
      )(new Optimizer[SpireUnitaryOperator, SpireBinaryOperator, Complex[Double], XAndLambda] {
        def rules: List[PartialFunction[SpireNode[Complex[Double], XAndLambda], SpireNode[Complex[Double], XAndLambda]]] =
          List(
            {
              case BinaryNode(Power, left, ConstantNode(Complex(2.0, 0.0))) => BinaryNode(Times, left, left)
            }
          )
      })
    val derived = Language.fLang.optimize(Language.fLang.derive(iteration)(X))

    val initial = Language.c0Lang.optimize(Language.c0Lang.parse(n.initial).get)

    val z = RefVec2("z")
    val fzlast = RefVec2("fzlast")
    val fz = RefVec2("fz")
    val fderz = RefVec2("fderz")

    val functionLangNames: PartialFunction[XAndLambda, Ref[WebGlTypeVec2.type]] = {
      case X => z
      case Lambda => inputVar
    }

    val initialLangNames: PartialFunction[Lambda.type, RefExp[WebGlTypeVec2.type]] = {
      case Lambda => RefExp(inputVar)
    }

    s"""{
       |  int l = 0;
       |  ${WebGlType.declare(z, WebGlExpression.toExpression(initial, initialLangNames))}
       |  ${WebGlType.declare(fz, WebGlType.zero[WebGlTypeVec2.type])}
       |  {
       |  ${WebGlStatement.assign(fz, iteration, functionLangNames).map(_.toCode).mkString("\n")}
       |  }
       |  ${WebGlType.declare(fzlast, RefExp(fz))}
       |  for(int i = 0;i< ${n.maxIterations}; i++){
       |    ${WebGlType.assign(fzlast, RefExp(fz))}
       |    {
       |    ${WebGlStatement.assign(fz, iteration, functionLangNames).map(_.toCode).mkString("\n")}
       |    }
       |    ${WebGlType.declare(fderz, WebGlType.zero[WebGlTypeVec2.type])}
       |    {
       |    ${WebGlStatement.assign(fderz, derived, functionLangNames).map(_.toCode).mkString("\n")}
       |    }
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
    val initial = Language.c0Lang.optimize(Language.c0Lang.parse(n.initial).get)
    val iteration = Language.fLang.optimize(Language.fLang.parse(n.iteration).get)

    val z = RefVec2("z")

    val functionLangNames: PartialFunction[ZAndLambda, RefExp[WebGlTypeVec2.type]] = {
      case Z => RefExp(z)
      case Lambda => RefExp(inputVar)
    }

    val initialLangNames: PartialFunction[Lambda.type, RefExp[WebGlTypeVec2.type]] = {
      case Lambda => RefExp(inputVar)
    }

    s"""{
       |  int l = 0;
       |  ${WebGlType.declare(z, WebGlExpression.toExpression(initial, initialLangNames))}
       |  for(int i = 0;i< ${n.maxIterations}; i++){
       |    ${WebGlType.assign(z, WebGlExpression.toExpression(iteration, functionLangNames))}
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
