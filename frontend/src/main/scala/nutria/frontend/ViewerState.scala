package nutria.frontend

import nutria.core.FractalProgram

case class ViewerState(fractalProgram: FractalProgram,
                       dragStartPosition: Option[(Double, Double)] = None)