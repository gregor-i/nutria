package snabbdom

import org.scalajs.dom
import org.scalajs.dom.Event
import org.scalajs.dom.raw.HTMLElement

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSGlobal, JSImport}
import scala.scalajs.js.{Dictionary, UndefOr, |}

@js.native
@JSImport("snabbdom", JSImport.Namespace)
object SnabbdomFacade extends js.Object {

  type PatchFunction = js.Function2[VNode | dom.Element, VNode, VNode]

  // note: remove is not considered
  type Hook          = js.Function0[Unit] | js.Function1[VNode, Unit] | js.Function2[VNode, VNode, Unit]
  type Eventlistener = js.Function1[_ <: Event, Unit]
  type Child         = String | VNode
  type Key           = UndefOr[String | Double | Int]

  def h(sel: String, props: Data, children: js.Array[Child]): VNode = js.native

  def init(modules: js.Array[_]): PatchFunction = js.native
}

@JSImport("snabbdom/modules/class.js", JSImport.Default)
@js.native
private object ClassModule extends js.Object

@JSImport("snabbdom/modules/attributes.js", JSImport.Default)
@js.native
private object AttrsModule extends js.Object

@JSImport("snabbdom/modules/dataset.js", JSImport.Default)
@js.native
private object DatasetModule extends js.Object

@JSImport("snabbdom/modules/eventlisteners.js", JSImport.Default)
@js.native
private object EventListenerModule extends js.Object

@JSImport("snabbdom/modules/props.js", JSImport.Default)
@js.native
private object PropsModule extends js.Object

@JSImport("snabbdom/modules/style.js", JSImport.Default)
@js.native
private object StyleModule extends js.Object

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

private[snabbdom] class Data(
    val key: SnabbdomFacade.Key,
    val `class`: Dictionary[Boolean],
    val props: Dictionary[js.Any],
    val attrs: Dictionary[String],
    val dataset: Dictionary[String],
    val style: Dictionary[String],
    val on: Dictionary[SnabbdomFacade.Eventlistener],
    val hook: Dictionary[SnabbdomFacade.Hook]
) extends js.Object
