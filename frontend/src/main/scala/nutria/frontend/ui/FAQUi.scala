package nutria.frontend.ui

import nutria.frontend._
import snabbdom.Node

object FAQUi extends Page[FAQState] {
  def render(implicit state: FAQState, update: NutriaState => Unit) =
    Seq(common.Header(state, update), content, common.Footer())

  private val faqContent =
    """<h2>After zooming, the image becomes pixelated. Why is that?</h2>
      |<p>
      |  Nutria uses WebGL Fragment Shader to create the fractal images.
      |  WebGL 1.0 only supports <a href="https://en.wikipedia.org/wiki/Single-precision_floating-point_format">single precision floating point numbers</a>.
      |  There are no more numbers between 2 pixels, so the pixel have the same position and caculate the same pixel color.
      |</p>
    """.stripMargin

  private val moreContent =
    """<article class="message is-info">
      |  <div class="message-body">
      |    <span class="icon"><i class="fa fa-info-circle"></i></span>
      |    More questions and answers are coming soon.
      |  </div>
      |</article>
      |""".stripMargin

  private def content(implicit state: FAQState, update: NutriaState => Unit) =
    Node("div.container")
      .child(
        Node("div.section")
          .child(Node("h1.title.is-1").text("Frequently Asked Questions (FAQ)"))
          .child(Node("h2.subtitle").text(""))
      )
      .child(
        Node("div.section.content").prop("innerHTML", faqContent)
      )
      .child(
        Node("div.section").prop("innerHTML", moreContent)
      )

}
