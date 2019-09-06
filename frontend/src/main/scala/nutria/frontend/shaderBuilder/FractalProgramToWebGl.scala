package nutria.frontend.shaderBuilder

import mathParser.algebra._
import mathParser.implicits._
import mathParser.{BinaryNode, ConstantNode, Optimizer}
import nutria.core._
import nutria.core.languages.{CLang, CNode, Lambda, X, XAndLambda, Z, ZAndLambda, ZAndZDerAndLambda, ZDer}
import spire.math.Complex

object FractalProgramToWebGl {
  def apply(fractalProgram: FractalProgram): (RefVec2, RefVec4) => String =
    fractalProgram match {
      case f: NewtonIteration => newtonIteration(f)
      case f: DivergingSeries => divergingSeries(f)
      case f: DerivedDivergingSeries => derivedDivergingSeries(f)
      case f: FreestyleProgram => freestyle(f)
    }

  def derivedDivergingSeries(f: DerivedDivergingSeries)
                            (inputVar: RefVec2, outputVar: RefVec4) = {
    val z = RefVec2("z")
    val zNew = RefVec2("z_new")
    val zDer = RefVec2("z_der")
    val zDerNew = RefVec2("z_der_new")

    val iterationLangNames: PartialFunction[ZAndLambda, Ref[WebGlTypeVec2.type]] = {
      case Z => z
      case Lambda => inputVar
    }

    val iterationDerLangNames: PartialFunction[ZAndZDerAndLambda, Ref[WebGlTypeVec2.type]] = {
      case Z => z
      case ZDer => zDer
      case Lambda => inputVar
    }

    val initialLangNames: PartialFunction[Lambda.type, Ref[WebGlTypeVec2.type]] = {
      case Lambda => inputVar
    }

    s"""{
       |  int l = 0;
       |  ${WebGlStatement.blockDeclare(z, f.initialZ.node, initialLangNames)}
       |  ${WebGlStatement.blockDeclare(zDer, f.initialZDer.node, initialLangNames)}
       |  for(int i = 0; i < ${f.maxIterations}; i++){
       |    ${WebGlStatement.blockDeclare(zNew, f.iterationZ.node, iterationLangNames)}
       |    ${WebGlStatement.blockDeclare(zDerNew, f.iterationZDer.node, iterationDerLangNames)}
       |    ${WebGlType.assign(z, RefExp(zNew))}
       |    ${WebGlType.assign(zDer, RefExp(zDerNew))}
       |    if(dot(z,z) > float(${f.escapeRadius.value * f.escapeRadius.value}))
       |      break;
       |    l ++;
       |  }
       |
       |  if(l == ${f.maxIterations}){
       |    ${outputVar.name} = vec4(0.0, 0.0, 0.25, 1.0);
       |  }else{
       |    const float h2 = float(${f.h2});
       |    const vec2 v = vec2(float(${Math.cos(f.angle.value)}), float(${Math.sin(f.angle.value)}));
       |    vec2 u = normalize(complex_divide(${z.name}, ${zDer.name}));
       |    float t = max((dot(u, v) + h2) / (1.0 + h2), 0.0);
       |    ${outputVar.name} = mix(vec4(0.0, 0.0, 0.0, 1.0), vec4(1.0, 1.0, 1.0, 1.0), t);
       |  }
       |}
    """.stripMargin
  }


  def newtonIteration(n: NewtonIteration)(inputVar: RefVec2, outputVar: RefVec4): String = {
    val optimizer = new Optimizer[SpireUnitaryOperator, SpireBinaryOperator, Complex[Double], XAndLambda] {
      def rules: List[PartialFunction[CNode[XAndLambda], CNode[XAndLambda]]] =
        List({
          case BinaryNode(Power, left, ConstantNode(Complex(2.0, 0.0))) => BinaryNode(Times, left, left)
        })
    }

    val lang = implicitly[CLang[XAndLambda]]
    val iteration = lang.optimize(n.function.node)(optimizer)
    val derived = lang.optimize(lang.derive(iteration)(X))

    val initial = n.initial.node

    val z = RefVec2("z")
    val fzlast = RefVec2("fzlast")
    val fz = RefVec2("fz")
    val fderz = RefVec2("fderz")

    val functionLangNames: PartialFunction[XAndLambda, Ref[WebGlTypeVec2.type]] = {
      case X => z
      case Lambda => inputVar
    }

    val initialLangNames: PartialFunction[Lambda.type, Ref[WebGlTypeVec2.type]] = {
      case Lambda => inputVar
    }

    s"""{
       |  int l = 0;
       |  ${WebGlStatement.blockDeclare(z, initial, initialLangNames)}
       |  ${WebGlType.declare(fz, WebGlType.zero[WebGlTypeVec2.type])}
       |  ${WebGlStatement.blockAssign(fz, iteration, functionLangNames)}
       |  ${WebGlType.declare(fzlast, RefExp(fz))}
       |  for(int i = 0;i< ${n.maxIterations}; i++){
       |    ${WebGlType.assign(fzlast, RefExp(fz))}
       |    ${WebGlStatement.blockAssign(fz, iteration, functionLangNames)}
       |    ${WebGlStatement.blockDeclare(fderz, derived, functionLangNames)}
       |    ${z.name} -= ${FloatLiteral(n.overshoot.value.toFloat).toCode} * complex_divide(${fz.name}, ${fderz.name});
       |    if(length(${fz.name}) < ${FloatLiteral(n.threshold.value.toFloat).toCode})
       |      break;
       |    l ++;
       |  }
       |
       |  if(length(${fz.name}) < ${FloatLiteral(n.threshold.value.toFloat).toCode}){
       |    float fract = 0.0;
       |    if(fz == ${WebGlType.zero[WebGlTypeVec2.type].toCode}){
       |      fract = float(l - 1);
       |    }else{
       |      fract = float(l) - log(${n.threshold} / length(${fz.name})) / log( length(${fzlast.name}) / length(${fz.name}));
       |    }
       |
       |    float H = atan(z.x - ${FloatLiteral(n.center._1.toFloat).toCode}, z.y - ${FloatLiteral(n.center._2.toFloat).toCode}) / float(${2 * Math.PI});
       |    float V = exp(-fract / ${FloatLiteral(n.brightnessFactor.value.toFloat).toCode});
       |    float S = length(z);
       |
       |    ${outputVar.name} = vec4(hsv2rgb(vec3(H, S, V)), 1.0);
       |  }else{
       |    ${outputVar.name} = vec4(vec3(0.0), 1.0);
       |  }
       |}
       """.stripMargin
  }


  def divergingSeries(n: DivergingSeries)(inputVar: RefVec2, outputVar: RefVec4): String = {
    import nutria.core.languages._
    val initial = n.initial.node
    val iteration = n.iteration.node

    val z = RefVec2("z")

    val functionLangNames: PartialFunction[ZAndLambda, Ref[WebGlTypeVec2.type]] = {
      case Z => z
      case Lambda => inputVar
    }

    val initialLangNames: PartialFunction[Lambda.type, Ref[WebGlTypeVec2.type]] = {
      case Lambda => inputVar
    }

    s"""{
       |  int l = 0;
       |  ${WebGlStatement.blockDeclare(z, initial, initialLangNames)}
       |  for(int i = 0;i< ${n.maxIterations}; i++){
       |    ${WebGlStatement.blockAssign(z, iteration, functionLangNames)}
       |    if(length(${z.name}) > ${FloatLiteral(n.escapeRadius.value.toFloat).toCode})
       |      break;
       |    l ++;
       |  }
       |
       |  float fract = float(l) / float(${n.maxIterations});
       |  ${outputVar.name} = vec4(fract, fract, fract, 1.0);
       |}
       """.stripMargin
  }


  def freestyle(f: FreestyleProgram)(inputVar: RefVec2, outputVar: RefVec4): String = {
    s"""{
       |vec2 z = ${inputVar.name};
       |vec4 color = vec4(0.0, 0.0, 0.0, 0.0);
       |
       |${f.code}
       |
       |${outputVar.name} = color;
       |}
     """.stripMargin
  }
}
