package nutria.core

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

@monocle.macros.Lenses()
case class FractalEntity(description: String,
                         reference: Option[String],
                         program: FractalProgram)


object FractalEntity {
  val systemFractals = Vector[FractalEntity](
    FractalEntity(
      program = Mandelbrot(shaded = false),
      description = "the famous mandelbrot with escape time",
      reference = Some("https://en.wikipedia.org/wiki/Mandelbrot_set#Escape_time_algorithm")
    ),
    FractalEntity(
      program = Mandelbrot(),
      description = "the famous mandelbrot with Normal map effect",
      reference = Some("https://www.math.univ-toulouse.fr/~cheritat/wiki-draw/index.php/Mandelbrot_set#Normal_map_effect")
    ),
    FractalEntity(
      program = JuliaSet(c = (-0.6, 0.6), shaded = false),
      description = "",
      reference = None
    ),
    FractalEntity(
      program = JuliaSet(c = (-0.6, 0.6)),
      description = "",
      reference = None
    ),
    newton("x*x*x - 1", "lambda"),
    newton("x*x*x -x - 1", "lambda"),
    newton("x*x*x + 1/x - 1", "lambda"),
    newton("(x * x + lambda - 1) * x - lambda", "0"),
    newton("exp(x)-i", "lambda"),
    newton("(x * x + sin(lambda) - 1) * x - lambda", "0"),
    newtonMandelbrotPolynomial(2),
    newtonMandelbrotPolynomial(3),
    newtonMandelbrotPolynomial(4),
    newtonMandelbrotPolynomial(5),
  )

  private def newton(f: String, x0: String) =
    FractalEntity(
      program = NewtonIteration(function = f, initial = x0),
      description = s"newton iteration with f(x) = $f, x0 = $x0",
      reference = None
    )

  private def newtonMandelbrotPolynomial(n: Int) = {
    val p = NewtonIteration.mandelbrotPolynomial(n)
    FractalEntity(
      program = p,
      description = s"newton iteration over madelbrot polynomial($n) with f(x) = ${p.function}, x0 = ${p.initial}",
      reference = None
    )
  }

  implicit val decoder: Decoder[FractalEntity] = deriveDecoder
  implicit val encoder: Encoder[FractalEntity] = deriveEncoder
}


