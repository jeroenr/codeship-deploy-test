package nl.weeronline.hello.authentication

import akka.http.scaladsl.model.headers.{ Authorization, OAuth2BearerToken }
import akka.http.scaladsl.server.{ AuthorizationFailedRejection, _ }
import nl.weeronline.hello.Logging

import scala.concurrent.ExecutionContext.Implicits.global

trait AuthorizationDirectives extends Logging {
  self: Directives =>

  // TODO: dep injection
  private val tokenVerifier = new JwtTokenVerifier

  private def extractBearerToken(authHeader: Option[Authorization]): Option[String] =
    authHeader.collect {
      case Authorization(OAuth2BearerToken(token)) => token
    }

  def authorized: Directive1[AuthInfo] = {
    optionalHeaderValueByType(classOf[Authorization]).map(extractBearerToken).flatMap {
      case Some(token) =>
        onComplete(tokenVerifier.verifyToken(token)).flatMap { x =>
          x.map(authInfo => provide(authInfo))
            .recover {
              case ex =>
                log.error("Couldn't log in using provided authorization token", ex)
                reject(AuthorizationFailedRejection).toDirective[Tuple1[AuthInfo]]
            }
            .get
        }
      case None =>
        reject(AuthorizationFailedRejection)
    }
  }
}
