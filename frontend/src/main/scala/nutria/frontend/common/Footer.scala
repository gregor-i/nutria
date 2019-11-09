package nutria.frontend.common

import snabbdom.Snabbdom.h

object Footer {
  def apply() = h("footer.footer")(
    h("div.content.has-text-centered")(
      purpose,
      licence
    )
  )

  private val purpose = h("p")(
    h("strong")("nutria"),
    " is a project dedicated to the purpose of ",
    h("i")("exploring, understanding, implementing"),
    " and ultimately ",
    h("i")("rendering"),
    " ",
    h("a", attrs = Seq("href" -> "https://en.wikipedia.org/wiki/Fractal"))("fractals.")
  )

  private val licence = h("p")(
    "It is licenced under ",
    h("a", attrs = Seq("href" -> "https://raw.githubusercontent.com/gregor-i/nutria/master/LICENSE.md"))("GNU GENERAL PUBLIC LICENSE"),
    " and its source code is published on ",
    h("a", attrs = Seq("href" -> "https://github.com/gregor-i/nutria"))("github")
  )
}
