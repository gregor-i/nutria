package snabbdom

import org.scalajs.dom.Event

import scala.scalajs.js
import scala.scalajs.js.Dictionary

object Snabbdom {
  def init(classModule: Boolean = false,
           propsModule: Boolean = false,
           attributesModule: Boolean = false,
           datasetModule: Boolean = false,
           styleModule: Boolean = false,
           eventlistenersModule: Boolean = false) = {
    SnabbdomNative.init(
      js.Array(
        Some(ClassModule).filter(_ => classModule),
        Some(PropsModule).filter(_ => propsModule),
        Some(AttrsModule).filter(_ => attributesModule),
        Some(DatasetModule).filter(_ => datasetModule),
        Some(StyleModule).filter(_ => styleModule),
        Some(EventListenerModule).filter(_ => eventlistenersModule),
      ).flatMap(_.toSeq)
    )
  }

  def h(tag: String,
        key: SnabbdomNative.Key = js.undefined,
        classes: Seq[(String, Boolean)] = Seq.empty,
        props: Seq[(String, js.Any)] = Seq.empty,
        attrs: Seq[(String, String)] = Seq.empty,
        dataset: Seq[(String, String)] = Seq.empty,
        styles: Seq[(String, String)] = Seq.empty,
        events: Seq[(String, SnabbdomNative.Eventlistener)] = Seq.empty,
        hooks: Seq[(String, SnabbdomNative.Hook)] = Seq.empty
       )(
         children: SnabbdomNative.Child*
       ): VNode = {

    SnabbdomNative.h(tag = tag,
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
      js.Array(children: _*))
  }

  def event(f: Event => Unit): SnabbdomNative.Eventlistener = f: js.Function1[Event, Unit]
  def specificEvent[E <: Event](f: E => Unit): SnabbdomNative.Eventlistener = f: js.Function1[E, Unit]

  def hook(f: (VNode, VNode) => Unit): SnabbdomNative.Hook = f : js.Function2[VNode, VNode, Unit]
  def hook(f: VNode => Unit): SnabbdomNative.Hook = f : js.Function1[VNode, Unit]
}

