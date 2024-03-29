package nutria.frontend.pages
package common

import monocle.Lens
import nutria.frontend.util.{SnabbdomUtil, Updatable}
import snabbdom.Node
import snabbdom.components.Icon

import scala.util.chaining._

object Pagination {
  val itemsPerPage = 24

  def page[A, S](itemsLens: Lens[S, Seq[A]], pageLens: Lens[S, Int])(implicit updatable: Updatable[S, _]): Seq[A] = {
    val p = pageLens.get(updatable.state)
    itemsLens
      .get(updatable.state)
      .slice((p - 1) * itemsPerPage, p * itemsPerPage)
  }

  def links[A, S](itemsLens: Lens[S, Seq[A]], pageLens: Lens[S, Int])(implicit updatable: Updatable[S, S]): Node = {
    val page  = pageLens.get(updatable.state)
    val pages = (itemsLens.get(updatable.state).size - 1) / itemsPerPage + 1

    def action(p: Int) =
      SnabbdomUtil.modify(pageLens.set(p))

    def isValid(p: Int) =
      p >= 1 && p <= pages

    def link(p: Int) =
      "li"
        .key(p)
        .child(
          "a.pagination-link"
            .`class`("is-current", p == page)
            .text(p.toString)
            .event("click", action(p))
        )

    val range = (page - 2).max(1) to (page + 2).min(pages)

    "nav.pagination.is-centered"
      .attr("role", "navigation")
      .attr("aria-label", "pagination")
      .child(
        "ul.pagination-list"
          .child(
            "a.pagination-link".child(Icon(Icons.prev)).boolAttr("disabled", !isValid(page - 1)).event("click", action(page - 1))
          )
          .pipe { node =>
            if (!range.contains(1) && !range.contains(2))
              node
                .child(link(1))
                .child(ellipsis)
            else
              node
          }
          .child(range.map(link))
          .pipe { node =>
            if (!range.contains(pages - 1) && !range.contains(pages))
              node
                .child(ellipsis)
                .child(link(pages))
            else
              node
          }
          .child(
            "a.pagination-link".child(Icon(Icons.next)).boolAttr("disabled", !isValid(page + 1)).event("click", action(page + 1))
          )
      )
  }

  private val ellipsis = "li".child("span.pagination-ellipsis".text("…"))
}
