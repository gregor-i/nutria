package nutria.frontend.common

import nutria.frontend.NutriaState
import nutria.frontend.shared.UserInfo
import org.scalajs.dom
import snabbdom.Snabbdom.h
import snabbdom.{Snabbdom, VNode}

object Header {
  def apply(pageTitle: String, user: Option[UserInfo])(implicit state: NutriaState, update: NutriaState => Unit): VNode =
    h("div.top-bar")(
      h("div")(
        brandIcon,
        h("span")(pageTitle)
      ),
      user.fold(loggedOutActions)(loggedInActions)
    )

  private val brandIcon = h("img.brand-icon",
    styles = Seq(
      "height" -> "100%",
      "cursor" -> "pointer",
    ),
    attrs = Seq("src" -> "/img/icon.png"),
    events = Seq("click" -> Snabbdom.event(_ => dom.window.location.replace("/"))),
  )()

  private val loggedOutActions =
    h("div")(
      h("a.button", attrs = Seq("href" -> "/auth/google"))(
        h("span.icon")(
          h("i.fa.fa-sign-in")()
        ),
        h("span")("Log in with Google")
      )
    )

  private def loggedInActions(user: UserInfo) =
    h("div.buttons.has-addons")(
      h("a.button.is-outlined")(
        h("span.icon")(
          h("img",
            attrs = Seq("src" -> user.picture),
            styles = Seq("border-radius" -> "50%")
          )()
        ),
        h("span")(s"Profile")
      ),
      h("a.button.is-outlined", attrs = Seq("href" -> "/auth/logout"))(
        h("span.icon")(
          h("i.fa.fa-sign-out")()
        ),
        h("span")(s"Log out"),
      )
    )
}
