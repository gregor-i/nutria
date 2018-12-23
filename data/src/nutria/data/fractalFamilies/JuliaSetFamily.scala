package nutria.data.fractalFamilies

import nutria.data.sequences._

class JuliaSetFamily(juliaSet: JuliaSet) extends Family(juliaSet.toString, juliaSet(100))
