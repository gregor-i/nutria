package nutria.frontend.pages.common

import monocle.{Iso, Lens}
import nutria.api.Entity
import nutria.frontend.NutriaState
import snabbdom.Node

object EntityAttributes {
  def section[S, E](lens: Lens[S, Entity[E]])(implicit state: S, update: S => Unit): Node =
    Node("section.section")
      .child(Node("h4.title.is-4").text("Administration Attributes:"))
      .child(form(lens))

  def form[S, E](lens: Lens[S, Entity[E]])(implicit state: S, update: S => Unit): Seq[Node] = {
    Seq(
      Form.forLens("Title", lens composeLens Entity.title),
      Form.forLens("Description", lens composeLens Entity.description),
      Form.forLens(
        "References",
        lens composeLens Entity.reference composeIso Iso[List[String], String](
          _.mkString(" ")
        )(_.split("\\s").filter(_.nonEmpty).toList)
      ),
      Form.readonlyStringInput("Published", if (lens.get(state).published) "published" else "private")
    )
  }
}
