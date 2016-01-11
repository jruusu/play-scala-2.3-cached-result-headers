package controllers

import play.api.cache.Cached
import play.api.mvc._

import play.api.Play.current

object Application extends Controller {

  def index = Cached("index") {
    Action {
      Ok(views.html.index("Your new application is ready."))
    }
  }

}