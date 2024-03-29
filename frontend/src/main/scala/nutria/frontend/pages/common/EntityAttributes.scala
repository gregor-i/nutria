package nutria.frontend.pages
package common

import monocle.{Iso, Lens}
import nutria.api.Entity
import nutria.frontend.util.Updatable
import snabbdom.Node

object EntityAttributes {
  def section[S, E](lens: Lens[S, Entity[E]])(implicit updatable: Updatable[S, S]): Node =
    "section.section"
      .child("h4.title.is-4".text("Administration Attributes:"))
      .child(form(lens))

  def form[S, E](lens: Lens[S, Entity[E]])(implicit updatable: Updatable[S, S]): Seq[Node] = {
    Seq(
      Form.forLens("Title", description = "give this fractal a short name", lens = lens composeLens Entity.title),
      Form.forLens(
        "Description",
        description = "give this fractal a short description",
        lens = lens composeLens Entity.description
      ),
      Form.forLens(
        "References",
        description = "if there is a detailed explanation somewhere on the web, you can post the links here",
        lens = lens composeLens Entity.reference composeIso Iso[List[String], String](
          _.mkString(" ")
        )(_.split("\\s").filter(_.nonEmpty).toList)
      ),
      Form.readonlyStringInput("Published", if (lens.get(updatable.state).published) "published" else "private")
    )
  }
}
