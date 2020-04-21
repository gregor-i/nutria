package nutria.shaderBuilder

import mathParser.implicits._
import nutria.core._
import nutria.core.languages.{Lambda, X, XAndLambda, Z, ZAndLambda, ZAndZDerAndLambda, ZDer}
import mathParser.Syntax._
import mathParser.algebra.SpireNode
import spire.math.Complex

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
       |    ${Assignment(outputVar, Vec4.fromRGBA(coloring.colorInside)).toCode}
       |  }else{
       |    const float h2 = float(${coloring.h2});
       |    const vec2 v = vec2(float(${Math.cos(coloring.angle.value)}), float(${Math.sin(coloring.angle.value)}));
       |    vec2 u = normalize(complex_divide(${z.name}, ${zDer.name}));
       |    float t = max((dot(u, v) + h2) / (1.0 + h2), 0.0);
       |    vec4 color_shadow = ${Vec4.fromRGBA(coloring.colorShadow).toCode};
       |    vec4 color_light = ${Vec4.fromRGBA(coloring.colorLight).toCode};
       |    ${outputVar.name} = mix(color_shadow, color_light, t);
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
       |  float pixel_distance = length((u_view_A + u_view_B) / u_resolution);
       |  float distance_factor = ${FloatLiteral(coloring.distanceFactor.value).toCode} / pixel_distance;
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
       |    ${Assignment(outputVar, Vec4.fromRGBA(coloring.colorInside)).toCode}
       |  }else{
       |    float z_length = length(z);
       |    float z_der_length = length(z_der);
       |    float d = distance_factor * 2.0 * z_length / z_der_length * log(z_length);
       |    vec4 color_far = ${Vec4.fromRGBA(coloring.colorFar).toCode};
       |    vec4 color_near = ${Vec4.fromRGBA(coloring.colorNear).toCode};
       |    ${outputVar.name} = mix(color_near, color_far, d);
       |  }
       |}
    """.stripMargin
  }

  def newtonIteration(n: NewtonIteration)(inputVar: RefVec2, outputVar: RefVec4): String = {

    def newtonIteration: SpireNode[Complex[Double], XAndLambda] = {
      val iteration = n.function.node.optimize(PowerOptimizer.optimizer)
      val derived   = n.function.node.derive(X).optimize(PowerOptimizer.optimizer)
      import mathParser.algebra.SpireLanguage.syntax._
      val lang = languages.xAndLambda
      lang.variable(X) - (lang.constantNode(n.overshoot.value) * iteration / derived)
    }

    val initial = n.initial.node.optimize(PowerOptimizer.optimizer)

    val z     = RefVec2("z")
    val zlast = RefVec2("zlast")

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
       |  ${WebGlType.declare(zlast, RefExp(z))}
       |  float delta;
       |  for(int i = 0;i< ${n.maxIterations}; i++){
       |    ${WebGlType.assign(zlast, RefExp(z))}
       |    ${WebGlStatement.blockAssign(z, newtonIteration, functionLangNames)}
       |    delta = length(z - zlast);
       |    if(delta < ${FloatLiteral(n.threshold.value).toCode})
       |      break;
       |    l ++;
       |  }
       |
       |  float precomputed_const = ${FloatLiteral(1.0 / Math.log(n.threshold.value)).toCode};
       |  float fract = float(l);
       |  if(delta != 0.0)
       |    fract = fract - log2(log(pow(delta, precomputed_const)));
       |
       |  float H = atan(z.x - ${FloatLiteral(n.center._1).toCode},
       |                 z.y - ${FloatLiteral(n.center._2).toCode}) / ${FloatLiteral((2 * Math.PI)).toCode};
       |  float V = exp(-fract / ${FloatLiteral(n.brightnessFactor.value).toCode});
       |  float S = length(z);
       |
       |  ${outputVar.name} = vec4(hsv2rgb(vec3(H, S, V)), 1.0);
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
       |    if(length(${z.name}) > ${FloatLiteral(n.escapeRadius.value).toCode})
       |      break;
       |    l ++;
       |  }
       |
       |  vec4 color_inside = ${Vec4.fromRGBA(coloring.colorInside).toCode};
       |  vec4 color_outside = ${Vec4.fromRGBA(coloring.colorOutside).toCode};
       |  float fract = float(l) / float(${n.maxIterations});
       |  ${outputVar.name} = mix(color_inside, color_outside, fract);
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
