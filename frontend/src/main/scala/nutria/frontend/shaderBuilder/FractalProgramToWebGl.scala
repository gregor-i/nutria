package nutria.frontend.shaderBuilder

import mathParser.implicits._
import nutria.core._
import nutria.core.languages.{Lambda, X, XAndLambda, Z, ZAndLambda, ZAndZDerAndLambda, ZDer}
import nutria.frontend.shaderBuilder.Syntax.EnrichNode

object FractalProgramToWebGl {
  def apply(fractalProgram: FractalProgram): (RefVec2, RefVec4) => String =
    fractalProgram match {
      case f: NewtonIteration                                           => newtonIteration(f)
      case f: DivergingSeries if f.coloring.isInstanceOf[TimeEscape]    => divergingSeries(f)
      case f: DivergingSeries if f.coloring.isInstanceOf[NormalMap]     => divergingSeriesNormalMap(f)
      case f: DivergingSeries if f.coloring.isInstanceOf[OuterDistance] => divergingSeriesOuterDistance(f)
      case f: FreestyleProgram                                          => freestyle(f)
    }

  def divergingSeriesNormalMap(f: DivergingSeries)(inputVar: RefVec2, outputVar: RefVec4) = {
    val coloring = f.coloring.asInstanceOf[NormalMap]
    val z        = RefVec2("z")
    val zNew     = RefVec2("z_new")
    val zDer     = RefVec2("z_der")
    val zDerNew  = RefVec2("z_der_new")

    val iterationLangNames: PartialFunction[ZAndLambda, Ref[WebGlTypeVec2.type]] = {
      case Z      => z
      case Lambda => inputVar
    }

    val iterationDerLangNames: PartialFunction[ZAndZDerAndLambda, Ref[WebGlTypeVec2.type]] = {
      case Z      => z
      case ZDer   => zDer
      case Lambda => inputVar
    }

    val initialLangNames: PartialFunction[Lambda.type, Ref[WebGlTypeVec2.type]] = {
      case Lambda => inputVar
    }

    val initialZ      = f.initial.node.optimize(PowerOptimizer.optimizer)
    val initialZDer   = f.initial.node.derive(Lambda).optimize(PowerOptimizer.optimizer)
    val iterationZ    = f.iteration.node.optimize(PowerOptimizer.optimizer)
    val iterationZDer = DivergingSeries.deriveIteration(f).optimize(PowerOptimizer.optimizer)

    s"""{
       |  int l = 0;
       |  ${WebGlStatement.blockDeclare(z, initialZ, initialLangNames)}
       |  ${WebGlStatement.blockDeclare(zDer, initialZDer, initialLangNames)}
       |  for(int i = 0; i < ${f.maxIterations}; i++){
       |    ${WebGlStatement.blockDeclare(zNew, iterationZ, iterationLangNames)}
       |    ${WebGlStatement.blockDeclare(zDerNew, iterationZDer, iterationDerLangNames)}
       |    ${WebGlType.assign(z, RefExp(zNew))}
       |    ${WebGlType.assign(zDer, RefExp(zDerNew))}
       |    if(dot(z,z) > float(${f.escapeRadius.value * f.escapeRadius.value}))
       |      break;
       |    l ++;
       |  }
       |
       |  if(l == ${f.maxIterations}){
       |    ${outputVar.name} = vec4(${Vec3.fromRGBA(coloring.colorInside).toCode}, 1.0);
       |  }else{
       |    const float h2 = float(${coloring.h2});
       |    const vec2 v = vec2(float(${Math.cos(coloring.angle.value)}), float(${Math.sin(coloring.angle.value)}));
       |    vec2 u = normalize(complex_divide(${z.name}, ${zDer.name}));
       |    float t = max((dot(u, v) + h2) / (1.0 + h2), 0.0);
       |    vec3 color_shadow = ${Vec3.fromRGBA(coloring.colorShadow).toCode};
       |    vec3 color_light = ${Vec3.fromRGBA(coloring.colorLight).toCode};
       |    ${outputVar.name} = mix(vec4(color_shadow, 1.0), vec4(color_light, 1.0), t);
       |  }
       |}
    """.stripMargin
  }

  def divergingSeriesOuterDistance(f: DivergingSeries)(inputVar: RefVec2, outputVar: RefVec4) = {
    val coloring = f.coloring.asInstanceOf[OuterDistance]
    val z        = RefVec2("z")
    val zNew     = RefVec2("z_new")
    val zDer     = RefVec2("z_der")
    val zDerNew  = RefVec2("z_der_new")

    val iterationLangNames: PartialFunction[ZAndLambda, Ref[WebGlTypeVec2.type]] = {
      case Z      => z
      case Lambda => inputVar
    }

    val iterationDerLangNames: PartialFunction[ZAndZDerAndLambda, Ref[WebGlTypeVec2.type]] = {
      case Z      => z
      case ZDer   => zDer
      case Lambda => inputVar
    }

    val initialLangNames: PartialFunction[Lambda.type, Ref[WebGlTypeVec2.type]] = {
      case Lambda => inputVar
    }

    val initialZ      = f.initial.node.optimize(PowerOptimizer.optimizer)
    val initialZDer   = f.initial.node.derive(Lambda).optimize(PowerOptimizer.optimizer)
    val iterationZ    = f.iteration.node.optimize(PowerOptimizer.optimizer)
    val iterationZDer = DivergingSeries.deriveIteration(f).optimize(PowerOptimizer.optimizer)

    s"""{
       |  int l = 0;
       |  ${WebGlStatement.blockDeclare(z, initialZ, initialLangNames)}
       |  ${WebGlStatement.blockDeclare(zDer, initialZDer, initialLangNames)}
       |  for(int i = 0; i < ${f.maxIterations}; i++){
       |    ${WebGlStatement.blockDeclare(zNew, iterationZ, iterationLangNames)}
       |    ${WebGlStatement.blockDeclare(zDerNew, iterationZDer, iterationDerLangNames)}
       |    ${WebGlType.assign(z, RefExp(zNew))}
       |    ${WebGlType.assign(zDer, RefExp(zDerNew))}
       |    if(dot(z,z) > float(${f.escapeRadius.value * f.escapeRadius.value}))
       |      break;
       |    l ++;
       |  }
       |
       |  if(l == ${f.maxIterations}){
       |    ${outputVar.name} = vec4(${Vec3.fromRGBA(coloring.colorInside).toCode}, 1.0);
       |  }else{
       |    float z_length = length(z);
       |    float z_der_length = length(z_der);
       |    float d = 2.0 * z_length / z_der_length * log(z_length);
       |    ${outputVar.name} = vec4(vec3(d*1000.0), 1.0); // todo: do something smart here to calculate the factor
       |    //color = mix(vec4(color_shadow, 1.0), vec4(color_light, 1.0), d);
       |  }
       |}
    """.stripMargin
  }

  def newtonIteration(n: NewtonIteration)(inputVar: RefVec2, outputVar: RefVec4): String = {
    val iteration = n.function.node.optimize(PowerOptimizer.optimizer)
    val derived   = n.function.node.derive(X).optimize(PowerOptimizer.optimizer)
    val initial   = n.initial.node.optimize(PowerOptimizer.optimizer)

    val z      = RefVec2("z")
    val fzlast = RefVec2("fzlast")
    val fz     = RefVec2("fz")
    val fderz  = RefVec2("fderz")

    val functionLangNames: PartialFunction[XAndLambda, Ref[WebGlTypeVec2.type]] = {
      case X      => z
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
       |    float H = atan(z.x - ${FloatLiteral(n.center._1.toFloat).toCode}, z.y - ${FloatLiteral(
         n.center._2.toFloat
       ).toCode}) / float(${2 * Math.PI});
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
    val coloring = n.coloring.asInstanceOf[TimeEscape]
    val z        = RefVec2("z")

    val functionLangNames: PartialFunction[ZAndLambda, Ref[WebGlTypeVec2.type]] = {
      case Z      => z
      case Lambda => inputVar
    }

    val initialLangNames: PartialFunction[Lambda.type, Ref[WebGlTypeVec2.type]] = {
      case Lambda => inputVar
    }

    s"""{
       |  int l = 0;
       |  ${WebGlStatement.blockDeclare(
         z,
         n.initial.node.optimize(PowerOptimizer.optimizer),
         initialLangNames
       )}
       |  for(int i = 0;i< ${n.maxIterations}; i++){
       |    ${WebGlStatement.blockAssign(
         z,
         n.iteration.node.optimize(PowerOptimizer.optimizer),
         functionLangNames
       )}
       |    if(length(${z.name}) > ${FloatLiteral(n.escapeRadius.value.toFloat).toCode})
       |      break;
       |    l ++;
       |  }
       |
       |  vec3 color_inside = ${Vec3.fromRGBA(coloring.colorInside).toCode};
       |  vec3 color_outside = ${Vec3.fromRGBA(coloring.colorOutside).toCode};
       |  float fract = float(l) / float(${n.maxIterations});
       |  ${outputVar.name} = vec4(mix(color_inside, color_outside, fract), 1.0);
       |}
       """.stripMargin
  }

  def freestyle(f: FreestyleProgram)(inputVar: RefVec2, outputVar: RefVec4): String = {
    val code = f.parameters.foldLeft(f.code) { (template, parameter) =>
      template.replaceAllLiterally("${" + parameter.name + "}", parameter.literal)
    }

    s"""{
       |vec2 z = ${inputVar.name};
       |vec4 color = vec4(0.0, 0.0, 0.0, 0.0);
       |
       |$code
       |
       |${outputVar.name} = color;
       |}
     """.stripMargin
  }
}
