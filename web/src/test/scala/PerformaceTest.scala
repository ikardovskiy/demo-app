import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class PerformanceTest extends Simulation {

  val httpProtocol = http
   // .baseUrl("http://localhost:8080")
    .baseUrl("http://11.168.99.100:8080")
    .userAgentHeader("Gatling")
    .shareConnections

  val scn = scenario("PerformanceTest")
    .repeat(1) {
      exec(http("GET /rest/code").get("/rest/code?country=rus"))
    }

  setUp(
    scn.inject(
      constantUsersPerSec(200) during (30 seconds) randomized)
  ).protocols(httpProtocol)
}