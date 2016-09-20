package viewportSelections

import io.ViewportImporter
import nutria.Viewport
import nutria.viewport.Viewport.createViewportByLongs

import scala.io.Source

object ViewportSelection {

  val benchmark = createViewportByLongs(0x3fd6c3283cc3c79dL, 0x3fd60d5e21097162L, 0xbea464307e900000L, 0xbea37ed68c080000L, 0x3e9d6a190a600000L, 0xbe9a4331f1900000L)
  val wallpaper = createViewportByLongs(0x3fd68b79b675ea99L, 0x3fd6049ba09a9cbdL, 0x3f0213f1a2e7c000L, 0xbeb691b33b140000L, 0xbe945fe581d00000L, 0xbef66fd8ae430000L)

  val selection = ViewportImporter(Source.fromFile("processor/resources/selection.json").mkString).getOrElse(throw new AssertionError("collection was not decoded"))

  val focusIteration1 = ViewportImporter(Source.fromFile("processor/resources/focus1.json").mkString).getOrElse(throw new AssertionError("collection was not decoded"))

  val focusIteration2 = ViewportImporter(Source.fromFile("processor/resources/focus2.json").mkString).getOrElse(throw new AssertionError("collection was not decoded"))
}