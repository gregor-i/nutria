package snabbdom

import snabbdom.SnabbdomFacade.Child

import scala.scalajs.js
import scala.scalajs.js.{Dictionary, |}

case class Node(
    private val sel: String,
    private val key: SnabbdomFacade.Key = js.undefined,
    private val classes: Seq[(String, Boolean)] = Seq.empty,
    private val props: Seq[(String, js.Any)] = Seq.empty,
    private val attrs: Seq[(String, String)] = Seq.empty,
    private val dataset: Seq[(String, String)] = Seq.empty,
    private val styles: Seq[(String, String)] = Seq.empty,
    private val events: Seq[(String, SnabbdomFacade.Eventlistener)] = Seq.empty,
    private val hooks: Seq[(String, SnabbdomFacade.Hook)] = Seq.empty,
    private val children: Seq[Child] = Seq.empty
) {
  def key(key: SnabbdomFacade.Key): Node =
    copy(key = key)

  def `class`(className: String, active: Boolean): Node =
    copy(classes = classes :+ (className -> active))

  def `class`(className: String): Node =
    `class`(className, active = true)

  def classes(classes: String*): Node =
    copy(classes = this.classes ++ classes.map(_ -> true))

  def prop(propName: String, value: js.Any): Node =
    copy(props = props :+ (propName -> value))

  def props(props: Seq[(String, js.Any)]): Node =
    copy(props = this.props ++ props)

  def attr(attrName: String, value: String): Node =
    copy(attrs = attrs :+ (attrName -> value))

  def boolAttr(attrName: String, value: Boolean): Node =
    if (value)
      copy(attrs = attrs :+ (attrName -> ""))
    else
      this

  def attrs(attrs: Seq[(String, String)]): Node =
    copy(attrs = this.attrs ++ attrs)

  def data(datasetName: String, value: String): Node =
    copy(dataset = dataset :+ (datasetName -> value))

  def dataset(dataset: Seq[(String, String)]): Node =
    copy(dataset = this.dataset ++ dataset)

  def style(styleName: String, value: String): Node =
    copy(styles = styles :+ (styleName -> value))

  def styles(styles: Seq[(String, String)]): Node =
    copy(styles = this.styles ++ styles)

  def event(on: String, listener: SnabbdomFacade.Eventlistener): Node =
    copy(events = events :+ (on -> listener))

  def events(events: Seq[(String, SnabbdomFacade.Eventlistener)]): Node =
    copy(events = this.events ++ events)

  def hook(hookName: String, hook: SnabbdomFacade.Hook): Node =
    copy(hooks = hooks :+ (hookName -> hook))

  def hooks(hooks: Seq[(String, SnabbdomFacade.Hook)]): Node =
    copy(hooks = this.hooks ++ hooks)

  def text(child: String): Node =
    copy(children = children :+ child)

  def child(node: Node): Node =
    copy(children = children :+ node.toVNode)

  def child(nodes: Iterable[Node]): Node =
    copy(children = children ++ nodes.map(_.toVNode))

  def childOptional(nodes: Option[Node]): Node =
    copy(children = children ++ nodes.map(_.toVNode))

  def children(nodes: (Node | Iterable[Node])*): Node =
    child(nodes.flatMap[Node] {
      case seq: Iterable[_] => seq.asInstanceOf[Iterable[Node]]
      case elem             => Seq(elem).asInstanceOf[Iterable[Node]]
    }: Iterable[Node])

  def toVNode: VNode = SnabbdomFacade.h(
    sel = sel,
    props = new Data(
      key = key,
      `class` = Dictionary(classes: _*),
      props = Dictionary(props: _*),
      attrs = Dictionary(attrs: _*),
      dataset = Dictionary(dataset: _*),
      style = Dictionary(styles: _*),
      on = Dictionary(events: _*),
      hook = Dictionary(hooks: _*)
    ),
    children = js.Array(children: _*)
  )
}
