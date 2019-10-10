package snabbdom

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

  def h(tagName: String,
        key: SnabbdomNative.Key = js.undefined,
        classes: Seq[(String, Boolean)] = Seq.empty,
        props: Seq[(String, js.Any)] = Seq.empty,
        attrs: Seq[(String, String)] = Seq.empty,
        dataset: Seq[(String, String)] = Seq.empty,
        styles: Seq[(String, String)] = Seq.empty,
        events: Seq[(String, SnabbdomNative.Eventlistener)] = Seq.empty,
        hooks: Seq[(String, SnabbdomNative.Hook)] = Seq.empty,
        child: SnabbdomNative.Child = js.undefined
       ): VNode = {

    SnabbdomNative.h(tag = tagName,
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
      child)
  }

}

