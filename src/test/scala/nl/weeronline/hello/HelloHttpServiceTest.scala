package nl.weeronline.hello

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{ Matchers, WordSpec }

import scala.concurrent.ExecutionContext

class HelloHttpServiceTest extends WordSpec with Matchers with ScalatestRouteTest with HelloHttpService {

  override val ec = ExecutionContext.global

  "WebServiceHttpApp" should {
    "be forbidden to access `/hello` unauthorized" in {
      Get("/hello") ~> helloRoute ~> check {
        status shouldBe StatusCodes.Forbidden
      }
    }
  }
}
