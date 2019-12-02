package nutria.frontend.ui.common

import snabbdom.Snabbdom.h

object Footer {
  def apply() = h("footer.footer")(
    h("div.content.has-text-centered")(
      licence
    )
  )

  private val licence = h("p")(
    "It is licenced under ",
    h("a", attrs = Seq("href" -> "https://raw.githubusercontent.com/gregor-i/nutria/master/LICENSE.md"))("GNU GENERAL PUBLIC LICENSE"),
    " and its source code is published on ",
    h("a", attrs = Seq("href" -> "https://github.com/gregor-i/nutria"))("github")
  )
}
