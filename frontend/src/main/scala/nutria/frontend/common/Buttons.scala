package nutria.frontend.common

import com.raquo.snabbdom.Modifier
import com.raquo.snabbdom.simple._
import com.raquo.snabbdom.simple.implicits._
import nutria.frontend.util.SnabbdomHelper

object Buttons extends SnabbdomHelper {
  private def apply(text: String, imgSrc: String)(additionals: Seq[Modifier[VNode, VNodeData]]) =
    tags.button(
      attrs.className := "button",
      tags.span(
        attrs.className := "icon",
        tags.img(attrs.src := imgSrc)
      ),
      tags.span(text),
      additionals
    )

  def group(buttons: VNode*): VNode =
    tags.div(
      attrs.className := "field has-addons",
      seqNode(buttons.map(tags.p(attrs.className := "control", _)))
    )

  def edit(additionals: Modifier[VNode, VNodeData]*) = apply("Edit", Images.edit)(additionals)
  def explore(additionals: Modifier[VNode, VNodeData]*) = apply("Explore", Images.explore)(additionals)
  def save(additionals: Modifier[VNode, VNodeData]*) = apply("Save", Images.upload)(additionals)
  def logSource(additionals: Modifier[VNode, VNodeData]*) = apply("Log Source", Images.info)(additionals)
  def accept(additionals: Modifier[VNode, VNodeData]*) = apply("Accept", Images.check)(additionals)
  def cancel(additionals: Modifier[VNode, VNodeData]*) = apply("Cancel", Images.cancel)(additionals)
  def share(additionals: Modifier[VNode, VNodeData]*) = apply("Share", Images.share)(additionals)
  def delete(additionals: Modifier[VNode, VNodeData]*) = apply("Delete", Images.delete)(additionals)
}
