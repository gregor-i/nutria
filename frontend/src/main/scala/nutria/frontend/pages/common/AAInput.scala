package nutria.frontend.pages
package common

import monocle.Lens
import nutria.core.AntiAliase
import nutria.frontend.{Context, PageState}
import snabbdom.Node

object AAInput {
  def apply[S <: PageState](lens: Lens[S, AntiAliase])(implicit context: Context[S]): Node =
    Form.forLens(
      label = "Anti-Aliasing",
      description = "Define the Anti-Aliasing factor. It should be something like 1, 2, 3 ...",
      lens = lens
    )
}
