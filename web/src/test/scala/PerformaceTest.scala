import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Random

class PerformanceTest extends Simulation {

  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    // .baseUrl("http://11.168.99.100:8080")
    .userAgentHeader("Gatling")
    .shareConnections


  val scn = scenario("PerformanceTest")
    .repeat(50) {
      exec(http("GET /rest/code").get(s"/rest/code").queryParam("country",_=>Random.alphanumeric.take(2).mkString))
     // exec(http("GET /rest/code").get(s"/rest/code").queryParam("country",_=>""))
    }

  setUp(
    scn.inject(
      constantUsersPerSec(200) during (10 seconds) randomized)
  ).protocols(httpProtocol)
}