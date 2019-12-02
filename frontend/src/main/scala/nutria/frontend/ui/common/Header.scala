package nutria.frontend.ui.common

import nutria.core.User
import nutria.frontend.{LoadingState, NutriaState}
import snabbdom.Snabbdom.h
import snabbdom.{Snabbdom, VNode}

object Header {
  def apply(pageTitle: String)(implicit state: NutriaState, update: NutriaState => Unit): VNode =
    h("div.top-bar")(
      h("div")(
        brandIcon,
        h("span")(pageTitle)
      ),
      state.user.fold(loggedOutActions)(loggedInActions)
    )

  private def brandIcon(implicit state: NutriaState, update: NutriaState => Unit) =
    h("img.brand-icon",
    styles = Seq(
      "height" -> "100%",
      "cursor" -> "pointer",
    ),
    attrs = Seq("src" -> "/img/icon.png"),
      events = Seq("click" -> Snabbdom.event(_ => update(LoadingState(NutriaState.libraryState())))),
  )()

  private val loggedOutActions =
    h("div")(
      h("a.button", attrs = Seq("href" -> "/auth/google"))(
        Icons.icon(Icons.login),
        h("span")("Log in with Google")
      )
    )

  private def loggedInActions(user: User) =
    h("div.buttons.has-addons")(
      h("a.button.is-outlined", events = Seq("click" -> Snabbdom.event{_ => println(user)}))(
        h("span.icon")(
          h("img",
            attrs = Seq("src" -> user.picture),
            styles = Seq("border-radius" -> "50%")
          )()
        ),
        h("span")(s"Profile")
      ),
      h("a.button.is-outlined", attrs = Seq("href" -> "/auth/logout"))(
        Icons.icon(Icons.logout),
        h("span")(s"Log out"),
      )
    )
}
