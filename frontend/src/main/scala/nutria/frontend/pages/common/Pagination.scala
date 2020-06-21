package nutria.frontend.pages.common

import monocle.Lens
import nutria.frontend.util.SnabbdomUtil
import snabbdom.{Node, Snabbdom}
import scala.util.chaining._

object Pagination {
  // todo: make it 24 again
  val itemsPerPage = 2

  def page[A, S](itemsLense: Lens[S, Seq[A]], pageLens: Lens[S, Int])(implicit state: S): Seq[A] = {
    val p = pageLens.get(state)
    itemsLense
      .get(state)
      .slice((p - 1) * itemsPerPage, p * itemsPerPage)
  }

  def links[A, S](itemsLense: Lens[S, Seq[A]], pageLens: Lens[S, Int])(implicit state: S, update: S => Unit): Node = {
    val page  = pageLens.get(state)
    val pages = (itemsLense.get(state).size - 1) / itemsPerPage + 1

    def action(p: Int) =
      SnabbdomUtil.update(pageLens.set(p))

    def isValid(p: Int) =
      p >= 1 && p <= pages

    def link(p: Int) =
      Node("li")
        .key(p)
        .child(
          Node("a.pagination-link")
            .`class`("is-current", p == page)
            .text(p.toString)
            .event("click", action(p))
        )

    val range = ((page - 2).max(1) to (page + 2).min(pages))

    Node("nav.pagination.is-centered")
      .attr("role", "navigation")
      .attr("aria-label", "pagination")
      .child(
        Node("ul.pagination-list")
          .child(Node("a.pagination-link").child(Icons.icon(Icons.prev)).boolAttr("disabled", !isValid(page - 1)).event("click", action(page - 1)))
          .pipe { node =>
            if (!range.contains(2))
              node
                .child(link(1))
                .child(ellipsis)
            else
              node
          }
          .child(range.map(link))
          .pipe { node =>
            if (!range.contains(pages - 1))
              node
                .child(ellipsis)
                .child(link(pages))
            else
              node
          }
          .child(Node("a.pagination-link").child(Icons.icon(Icons.next)).boolAttr("disabled", !isValid(page + 1)).event("click", action(page + 1)))
      )
  }

  private val ellipsis = Node("li").child(Node("span.pagination-ellipsis").text("…"))

}
