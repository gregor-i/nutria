package nutria.frontend.pages

import java.time.{ZoneOffset, ZonedDateTime}

import mathParser.complex.Complex
import nutria.api.{Entity, User, WithId}
import nutria.core.{ComplexParameter, Examples, FractalImage, IntParameter, Viewport}
import nutria.frontend.{Context, GlobalState, PageState}

import scala.concurrent.Future

object TestData {
  private val fractalImage = FractalImage(template = Examples.timeEscape, viewport = Viewport.mandelbrot)
  private val fractalEntity = WithId(
    id = "id",
    owner = "owner",
    entity = Entity(
      value = fractalImage
    ),
    updatedAt = ZonedDateTime.now(ZoneOffset.UTC),
    insertedAt = ZonedDateTime.now(ZoneOffset.UTC)
  )
  private val publicFractals = Vector.fill(10)(fractalEntity)
  private val owner          = GlobalState(Some(User("owner", "name", "email", None)))
  private val user           = GlobalState(Some(User("user", "name", "email", None)))
  private val noUser         = GlobalState(None)

  val states: Seq[(String, PageState, GlobalState)] = Seq(
    ("Loading", LoadingState(Future.failed(new Exception)), noUser),
    ("Error", ErrorState("error message"), noUser),
    ("FAQ", DocumentationState.faq, user),
    ("Introduction", DocumentationState.introduction, user),
    ("Greeting", GreetingState(randomFractal = Entity(value = fractalImage)), user),
    ("Explorer-owner", ExplorerState(remoteFractal = Some(fractalEntity), fractalImage = Entity(value = fractalImage)), owner),
    ("Explorer-no-user", ExplorerState(remoteFractal = Some(fractalEntity), fractalImage = Entity(value = fractalImage)), noUser),
    ("Explorer-user", ExplorerState(remoteFractal = Some(fractalEntity), fractalImage = Entity(value = fractalImage)), user),
    ("Gallery", GalleryState(publicFractals = publicFractals, page = 1), noUser),
    ("Template-Editor", TemplateEditorState.initial, user),
    (
      "Template-Editor-new-parameter-Int",
      TemplateEditorState.initial
        .copy(newParameter = Some(IntParameter("name", description = "description", value = 5))),
      user
    ),
    (
      "Template-Editor-new-parameter-Complex",
      TemplateEditorState.initial
        .copy(newParameter = Some(ComplexParameter("name", description = "description", value = Complex(5, 2)))),
      user
    )
  )
}
