package nutria.core

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder, JsonObject}
import io.circe.syntax._

@monocle.macros.Lenses()
case class FractalEntity(program: FractalProgram,
                         description: String = "",
                         reference: Option[String] = None,
                        )


object FractalEntity {
  val systemFractals: Vector[FractalEntity] = Vector[FractalEntity](
    FractalEntity(
      program = DivergingSeries.mandelbrot
    ),
    FractalEntity(
      program = DerivedDivergingSeries.mandelbrot,
      description = "the famous mandelbrot with Normal map effect",
      reference = Some("https://www.math.univ-toulouse.fr/~cheritat/wiki-draw/index.php/Mandelbrot_set#Normal_map_effect")
    ),
    FractalEntity(
      program = DivergingSeries.juliaSet((-0.6, 0.6))
    ),
    FractalEntity(
      program = DerivedDivergingSeries.juliaSet((-0.6, 0.6))
    ),
    newton("x*x*x - 1", "lambda"),
    newton("x*x*x -x - 1", "lambda"),
    newton("x*x*x + 1/x - 1", "lambda"),
    newton("(x * x + lambda - 1) * x - lambda", "0"),
    newton("exp(x)-i", "lambda"),
    newton("(x * x + sin(lambda) - 1) * x - lambda", "0"),
    newtonMandelbrotPolynomial(3),
    newtonMandelbrotPolynomial(4),
    newtonMandelbrotPolynomial(5),
    FractalEntity(
      program = DivergingSeries(
        iteration = "z*z*z + (-0.12 + i*0.80)",
        initial = "lambda"
      )
    )
  )

  private def newton(f: String, x0: String) =
    FractalEntity(
      program = NewtonIteration(function = f, initial = x0)
    )

  private def newtonMandelbrotPolynomial(n: Int) = {
    val p = NewtonIteration.mandelbrotPolynomial(n)
    FractalEntity(
      program = p,
      description = s"newton iteration over mandelbrot polynomial($n) with f(x) = ${p.function}, x0 = ${p.initial}",
    )
  }

  implicit val decoder: Decoder[FractalEntity] = deriveDecoder
  implicit val encoder: Encoder[FractalEntity] = deriveEncoder
}


case class FractalEntityWithId(id: String,
                               entity: FractalEntity)

object FractalEntityWithId {
  implicit val encoder: Encoder[FractalEntityWithId] = Encoder[FractalEntityWithId] { row =>
    Encoder[FractalEntity].apply(row.entity)
      .deepMerge(JsonObject("id" -> row.id.asJson).asJson)
  }

  implicit val decode: Decoder[FractalEntityWithId] = Decoder[FractalEntityWithId] { json =>
    for {
      entity <- json.as[FractalEntity]
      id <- json.downField("id").as[String]
    } yield FractalEntityWithId(id, entity)
  }
}
