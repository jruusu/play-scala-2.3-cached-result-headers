package controllers

import org.joda.time.{Duration, DateTime}
import play.api.cache.Cached
import play.api.mvc._

import play.api.Play.current

object Application extends Controller {
  val dateFormat = play.api.http.dateFormat

  case class WithCacheControl(action: EssentialAction) extends EssentialAction {
    def apply(request: RequestHeader) = {
      implicit val executionContext = play.api.libs.concurrent.Execution.defaultContext
      action(request).map { result =>
        result.header.headers.get(EXPIRES).map(dateFormat.parseDateTime) match {
          case None => result
          case Some(expires) =>
            val now = DateTime.now()
            val maxAge = new Duration(now, expires).getStandardSeconds + 1
            result.withHeaders(
              (CACHE_CONTROL, s"max-age=$maxAge")
            )
        }
      }
    }
  }

  val key: (RequestHeader) => String = { case rh => rh.uri }

  val cacheDurationFromExpiresHeader: scala.PartialFunction[ResponseHeader, scala.concurrent.duration.Duration] = {
    case rh => rh.headers.get(EXPIRES).map(dateFormat.parseDateTime) match {
      case None => scala.concurrent.duration.Duration(5, scala.concurrent.duration.SECONDS)
      case Some(expires) =>
        val expiresIn = new Duration(DateTime.now(), expires).getStandardSeconds
        scala.concurrent.duration.Duration(expiresIn, scala.concurrent.duration.SECONDS)
    }
  }

  def index = WithCacheControl { Cached(key, cacheDurationFromExpiresHeader) {
    Action {
      val expires = DateTime.now().plusSeconds(5).toString(dateFormat)
      Ok(views.html.index("Your new application is ready."))
        .withHeaders((EXPIRES, expires))
    }
  }}

}