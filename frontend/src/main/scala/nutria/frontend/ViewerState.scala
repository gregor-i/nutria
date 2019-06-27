package nutria.frontend

import nutria.data.FractalProgram

case class ViewerState(fractalProgram: FractalProgram,
                       dragStartPosition: Option[(Double, Double)] = None)