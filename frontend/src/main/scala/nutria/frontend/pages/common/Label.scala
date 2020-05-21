package nutria.frontend.pages.common

import snabbdom.{Node, Snabbdom, SnabbdomFacade}

object Label {
  def apply(label: String, actions: Seq[Node], node: Node): Node =
    Node("div.field.is-horizontal")
      .child(
        Node("div.field-label.is-normal")
          .style("flexGrow", "2")
          .child(Node("label.label").text(label))
      )
      .child(
        Node("div.field-body").child(
          Node("div.field.is-grouped")
            .child(Node("p.control.is-expanded").child(node))
            .child(actions.map(el => Node("p.control").child(el)))
        )
      )
}
