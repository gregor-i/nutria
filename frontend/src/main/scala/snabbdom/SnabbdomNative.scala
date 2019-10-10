package snabbdom

import org.scalajs.dom
import org.scalajs.dom.Event
import org.scalajs.dom.raw.HTMLElement

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSGlobal, JSImport, ScalaJSDefined}
import scala.scalajs.js.{Dictionary, UndefOr, |}

@js.native
@JSImport("snabbdom", JSImport.Namespace)
object SnabbdomNative extends js.Object {

  type PatchFunction = js.Function2[VNode | dom.Element, VNode, VNode]

  // note: remove is not considered
  type Hook = js.Function0[Unit] | js.Function1[VNode, Unit] | js.Function2[VNode, VNode, Unit]
  type Eventlistener = js.Function1[Event, Unit]
  type Child = UndefOr[String | VNode | Seq[VNode]]
  type Key = UndefOr[String | Double | Int]

  def h(tag: String, props: Data, children: Child): VNode = js.native

  def init(modules: js.Array[_]): PatchFunction = js.native
}

@JSImport("snabbdom/modules/class.js", JSImport.Default)
@js.native
object ClassModule extends js.Object

@JSImport("snabbdom/modules/attributes.js", JSImport.Default)
@js.native
object AttrsModule extends js.Object

@JSImport("snabbdom/modules/dataset.js", JSImport.Default)
@js.native
object DatasetModule extends js.Object

@JSImport("snabbdom/modules/eventlisteners.js", JSImport.Default)
@js.native
object EventListenerModule extends js.Object

@JSImport("snabbdom/modules/props.js", JSImport.Default)
@js.native
object PropsModule extends js.Object

@JSImport("snabbdom/modules/style.js", JSImport.Default)
@js.native
object StyleModule extends js.Object

 class Data(
              val key: SnabbdomNative.Key,
              val `class` : Dictionary[Boolean],
              val props : Dictionary[js.Any],
              val attrs: Dictionary[String],
              val dataset: Dictionary[String],
              val style: Dictionary[String],
              val on: Dictionary[SnabbdomNative.Eventlistener],
              val hook: Dictionary[SnabbdomNative.Hook]
            ) extends js.Object

@JSGlobal
@js.native
abstract class VNode extends js.Object {
  def sel: String
  def data: Data
  def children: UndefOr[Seq[VNode]]
  def text: UndefOr[String]
  def elm: UndefOr[HTMLElement]
  def key: UndefOr[String | Double | Int]
}