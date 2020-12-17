package nutria.frontend.pages
package common

import snabbdom.Node

object Label {
  def apply(label: String, description: String = "", actions: Seq[Node], node: Node): Node =
    "div.field.is-horizontal"
      .child(
        "div.field-label.is-normal"
          .style("flexGrow", "2")
          .child("label.label".text(label))
      )
      .child(
        "div.field-body"
          .child(
            "div.field"
              .child(
                "div.field.is-grouped"
                  .child("p.control.is-expanded".child(node))
                  .child(actions.map(el => "p.control".child(el)))
              )
              .childOptional(
                Some(description).filter(_.nonEmpty).map(text => "p.help".text(text))
              )
          )
      )

}
