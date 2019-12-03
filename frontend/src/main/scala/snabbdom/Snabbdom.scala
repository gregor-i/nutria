package snabbdom

import org.scalajs.dom.Event
import snabbdom.SnabbdomFacade.Child

import scala.scalajs.js
import scala.scalajs.js.{Dictionary, |}

object Snabbdom {
  def init(classModule: Boolean = false,
           propsModule: Boolean = false,
           attributesModule: Boolean = false,
           datasetModule: Boolean = false,
           styleModule: Boolean = false,
           eventlistenersModule: Boolean = false) =
    SnabbdomFacade.init(
      js.Array(
        Some(ClassModule).filter(_ => classModule),
        Some(PropsModule).filter(_ => propsModule),
        Some(AttrsModule).filter(_ => attributesModule),
        Some(DatasetModule).filter(_ => datasetModule),
        Some(StyleModule).filter(_ => styleModule),
        Some(EventListenerModule).filter(_ => eventlistenersModule),
      ).collect { case Some(module) => module }
    )

  def h(tag: String,
        key: SnabbdomFacade.Key = js.undefined,
        classes: Seq[(String, Boolean)] = Seq.empty,
        props: Seq[(String, js.Any)] = Seq.empty,
        attrs: Seq[(String, String)] = Seq.empty,
        dataset: Seq[(String, String)] = Seq.empty,
        styles: Seq[(String, String)] = Seq.empty,
        events: Seq[(String, SnabbdomFacade.Eventlistener)] = Seq.empty,
        hooks: Seq[(String, SnabbdomFacade.Hook)] = Seq.empty
       )(
         children: (Child | Seq[Child])*
       ): VNode = {

    SnabbdomFacade.h(sel = tag,
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
      js.Array(children.flatMap[Child] {
        case seq: Seq[_] => seq.asInstanceOf[Seq[Child]]
        case elem => Seq(elem).asInstanceOf[Seq[Child]]
      }: _*))
  }

  def event(f: Event => Unit): SnabbdomFacade.Eventlistener = f: js.Function1[Event, Unit]

  def specificEvent[E <: Event](f: E => Unit): SnabbdomFacade.Eventlistener = f: js.Function1[E, Unit]

  def hook(f: (VNode, VNode) => Unit): SnabbdomFacade.Hook = f: js.Function2[VNode, VNode, Unit]

  def hook(f: VNode => Unit): SnabbdomFacade.Hook = f: js.Function1[VNode, Unit]
}

