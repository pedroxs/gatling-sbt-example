package example

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

/**
 * Created by Pedro on 14/07/2015.
 */
class DuckDuckGoSimulation extends Simulation {

  object Search {
    val search = exec(http("Search")
      .get("/")
      .queryParam("q", "test")
      .check(status.is(200)))
  }

  val httpConf = http
    .baseURL("https://duckduckgo.com")
    .inferHtmlResources()
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .connection("keep-alive")
    .userAgentHeader("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0")

  val users = scenario("Users").exec(Search.search)

  setUp(
    users.inject(rampUsers(5) over (10 seconds))
  ).protocols(httpConf)

}
