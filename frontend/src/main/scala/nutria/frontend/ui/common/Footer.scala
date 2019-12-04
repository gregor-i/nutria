package nutria.frontend.ui.common

import snabbdom.Builder

object Footer {
  def apply() =
    Builder("footer.footer")
      .child(
        Builder("div.content.has-text-centered")
          .prop("innerHtml", licence)
      )
    .toVNode

  private val licence =
    """<p>
      |  This project is licenced under <a href="https://raw.githubusercontent.com/gregor-i/nutria/master/LICENSE.md">GNU GENERAL PUBLIC LICENSE</a>
      |  and its source code is published on <a href="https://github.com/gregor-i/nutria">github</a>
      |</p>
      |""".stripMargin
}
