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

  def index = WithCacheControl { Cached("index") {
    Action {
      Ok(views.html.index("Your new application is ready."))
    }
  }}

}