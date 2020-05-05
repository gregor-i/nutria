package nutria.staticRenderer

import nutria.SystemFractals
import nutria.core.languages.{StringFunction, XAndLambda, ZAndLambda}
import nutria.core.viewport.{Dimensions, Viewport}
import nutria.core._
import org.scalatest.funsuite.{AnyFunSuite, AsyncFunSuite}

class FreestyleSpec extends RenderingSuite {

  private def isSystemFractal(program: FractalProgram) = {
    assert(
      SystemFractals.systemFractals.exists(_.program == program),
      "fractal was not included in systemfractals. Maybe you want to update them?\nJson:\n" + FractalProgram.codec(program).noSpaces
    )
  }

  test("Sierpinski Triangle") {
    val program = FreestyleProgram(
      parameters = Seq(
        IntParameter(name = "iterations", value = 25),
        FloatParameter(name = "size", value = 1.0.toFloat),
        RGBParameter(name = "color_outside", value = RGB.white),
        RGBParameter(name = "color_inside", value = RGB.black)
      ),
      code = """
        |#define calc_tri_area(A, B, C) float((((A.x - C.x) * (B.y - C.y)) - ((B.x - C.x) * (A.y - C.y))) / 2.0)
        |
        |const float pi = 3.141;
        |
        |vec2 A = vec2(size*sin( 60.0/180.0*pi), size*cos( 60.0/180.0*pi));
        |vec2 B = vec2(size*sin(180.0/180.0*pi), size*cos(180.0/180.0*pi));
        |vec2 C = vec2(size*sin(300.0/180.0*pi), size*cos(300.0/180.0*pi));
        |
        |float area = calc_tri_area(A, B, C);
        |vec3 bary = vec3(calc_tri_area(B, C, p), calc_tri_area(C, A, p), calc_tri_area(A, B, p));
        |
        |if(bary.x > 0.0 || bary.y > 0.0 || bary.z > 0.0){
        |  // outside
        |  return vec4(color_outside, 1.0);
        |}else{
        |  // inside
        |  for(int i = 0; i < iterations; i++){
        |    bary = vec3(calc_tri_area(B, C, p), calc_tri_area(C, A, p), calc_tri_area(A, B, p));
        |    // inside
        |    if(bary.x / area > 0.5){
        |      B = 0.5 * (B + A);
        |      C = 0.5 * (C + A);
        |    } else if(bary.y / area > 0.5){
        |      // in area near B
        |      A = 0.5 * (A + B);
        |      C = 0.5 * (C + B);
        |    }else if(bary.z / area > 0.5){
        |      // in area near C
        |      A = 0.5 * (A + C);
        |      B = 0.5 * (B + C);
        |    }else{
        |      // in the middle triangle
        |      return vec4(mix(color_outside, color_inside, float(i)/float(iterations)), 1.0);
        |      break;
        |    }
        |    area /= 4.0;
        |  }
        |  return vec4(color_inside, 1.0);
        |}
        |""".stripMargin
    )

    isSystemFractal(program)

    Renderer
      .renderToFile(
        FractalImage(program, view = Viewport.aroundZero),
        dimensions = Dimensions.fullHD,
        fileName = s"$baseFolder/sierpinski-triangle.png"
      )
      .map(_ => succeed)
  }

  test("Lyapunov Fractal") {

    val program = FreestyleProgram(
      parameters = Seq(
        IntParameter(name = "iterations", value = 120),
        IntParameter(name = "steps_X", value = 6),
        IntParameter(name = "steps_Y", value = 6)
      ),
      code = """
         |float x = 0.5;
         |float h = 0.0;
         |
         |for( int i=0; i<iterations; i++ ){
         |  for(int i=0; i<steps_X; i++){
         |    x = p.x*x*(1.0-x); h += log2(abs(p.x*(1.0-2.0*x)));
         |  }
         |
         |  for(int i=0; i<steps_Y; i++){
         |    x = p.y*x*(1.0-x); h += log2(abs(p.y*(1.0-2.0*x)));
         |  }
         |}
         |
         |h /= float(iterations * (steps_X + steps_Y));
         |
         |vec3 col = vec3(0.0);
         |if( h<0.0 ){
         |  h = abs(h);
         |  col = (0.5 + 0.5*sin( vec3(0.0, 0.4, 0.7) + 2.5*h )) * pow(h,0.25);
         |}
         |return vec4(col, 1.0);
         |
         |""".stripMargin
    )

    isSystemFractal(program)

    Renderer
      .renderToFile(
        FractalImage(program, view = Viewport.aroundZero),
        dimensions = Dimensions.fullHD,
        fileName = s"$baseFolder/lyapunov-fractal.png"
      )
      .map(_ => succeed)
  }

  test("Koch Snowflake") {

    val program = FreestyleProgram(
      parameters = Seq(
        IntParameter(name = "iterations", value = 26),
        RGBAParameter(name = "color_inside", value = RGB.black.withAlpha()),
        RGBAParameter(name = "color_outside", value = RGB.white.withAlpha())
      ),
      code = """
               |vec2 z = p;
               |if(z.x < 0.0 || z.x > 1.0 || z.y < -0.5 || z.y > 0.5){
               |  return color_outside;
               |}else{
               |  z = abs(fract(z)-0.5);
               |  for(int i = 0; i < iterations ; i++){
               |    z += vec2(z.y*1.735, -z.x*1.735);
               |    z.x = abs(z.x)-0.58;
               |    z = -vec2(-z.y, z.x)*.865;
               |  }
               |
               |  if(z.x > 0.0){
               |    return color_outside;
               |  }else{
               |    return color_inside;
               |  }
               |}
               |""".stripMargin
    )

    isSystemFractal(program)

    Renderer
      .renderToFile(
        FractalImage(program, view = Viewport.aroundZero),
        dimensions = Dimensions.fullHD,
        fileName = s"$baseFolder/koch-snowflake.png"
      )
      .map(_ => succeed)
  }

  test("Nova Fractal") {

    val program = FreestyleProgram(
      parameters = Seq(
        IntParameter(name = "max_iterations", value = 200),
        NewtonFunctionParameter(name = "iteration", value = StringFunction.unsafe[XAndLambda]("x*x*x - 1"), includeDerivative = true)
      ),
      code = """vec2 lambda = p;
               |vec2 z = p;
               |int l = 0;
               |for(int i = 0; i<max_iterations; i++){
               |  vec2 delta = complex_divide(iteration(z, lambda), iteration_derived(z, lambda)) + p;
               |  z -= delta;
               |  if(length(delta) < float(0.001))
               |     break;
               |  l ++;
               |}
               |
               |return vec4(vec3(float(max_iterations - l) / float(max_iterations)), 1.0);
               |
               |""".stripMargin
    )

    isSystemFractal(program)

    Renderer
      .renderToFile(
        FractalImage(program, view = Viewport.aroundZero),
        dimensions = Dimensions.fullHD,
        fileName = s"$baseFolder/nova-fractal.png"
      )
      .map(_ => succeed)
  }
}
