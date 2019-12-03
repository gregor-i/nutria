package snabbdom

import snabbdom.SnabbdomFacade.Child

import scala.scalajs.js
import scala.scalajs.js.{Dictionary, |}

case class Builder(
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
  def key(key: String): Builder =
    copy(key = key)

  def `class`(className: String, active: Boolean): Builder =
    copy(classes = classes :+ (className -> active))

  def `class`(className: String): Builder =
    `class`(className,active =  true)

  def classes(classNames: String*): Builder =
    copy(classes = classes ++ classNames.map(_ -> true))

  def prop(propName: String, value: js.Any): Builder =
    copy(props = props :+ (propName -> value))

  def attr(attrName: String, value: String): Builder =
    copy(attrs = attrs :+ (attrName -> value))

  def data(datasetName: String, value: String): Builder =
    copy(dataset = dataset :+ (datasetName -> value))

  def style(styleName: String, value: String): Builder =
    copy(styles = styles :+ (styleName -> value))

  def event(on: String, listener: SnabbdomFacade.Eventlistener): Builder =
    copy(events = events :+ (on -> listener))

  def hook(hookName: String, hook: SnabbdomFacade.Hook): Builder =
    copy(hooks = hooks :+ (hookName -> hook))

  def child(child: Child): Builder =
    copy(children = children :+ child)

  def child(builder: Builder): Builder =
    child(builder.toVNode)

  def child(nodes: Iterable[Child]): Builder =
    copy(children = children ++ nodes)

  def children(nodes: (Child | Iterable[Child])*): Builder =
    child(nodes.flatMap[Child]{
      case seq: Iterable[_] => seq.asInstanceOf[Iterable[Child]]
      case elem => Seq(elem).asInstanceOf[Iterable[Child]]
    } : Iterable[Child])



  def toVNode: VNode = SnabbdomFacade.h(
    sel = sel,
    props = new Data(key = key,
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

object Builder {
  val body = Builder("body")
  val h1 = Builder("h1")
  val h2 = Builder("h2")
  val div = Builder("div")
  val p = Builder("p")
  val span = Builder("span")
  val a = Builder("a")
}