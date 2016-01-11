package controllers

import play.api.cache.Cached
import play.api.mvc._

import play.api.Play.current

object Application extends Controller {

  case class WithCacheControl(action: EssentialAction) extends EssentialAction {
    def apply(request: RequestHeader) = {
      implicit val executionContext = play.api.libs.concurrent.Execution.defaultContext
      action(request).map { result =>
        result.withHeaders(("Cache-Control", "max-age=123"))
      }
    }
  }

  val key: (RequestHeader) => String = { case rh => rh.uri }

  def index = WithCacheControl { Cached(key, 5) {
    Action {
      Ok(views.html.index("Your new application is ready."))
    }
  }}

}