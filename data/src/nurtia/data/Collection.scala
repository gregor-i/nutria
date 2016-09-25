package nurtia.data

import nutria.fractal.{Collatz, DoubleSequence, Mandelbrot, SequenceConstructor}

object Collection {

  val factories = Seq(
    SimpleFactory, AntiAliaseFactory, BuddhaBrotFactory
  )

  val fractals: Seq[(String, SequenceConstructor[_ <: DoubleSequence], Data[_])] =
    Seq(
      ("Mandelbrot", SequenceConstructor[Mandelbrot.Sequence], MandelbrotData),
      ("Collatz", SequenceConstructor[Collatz.Sequence], CollatzData)
      //      ("MandelbrotCube", MandelbrotCube, MandelbrotCube.fractals),
      //      ("Burning Ship", BurningShip, BurningShip.fractals),
      //      ("JuliaSet(-0.6, -0.6)", JuliaSet(-0.6, -0.6), JuliaSet(-0.6, -0.6).fractals),
      //      ("JuliaSet(-0.4, 0.6)", JuliaSet(-0.4, 0.6), JuliaSet(-0.4, 0.6).fractals),
      //      ("JuliaSet-0.8, 0.156)", JuliaSet(-0.8, 0.156), JuliaSet(-0.8, 0.156).fractals),
      //      ("Tricorn", Tricorn, Tricorn.fractals)
    )
}
