package example

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

/**
 * Created by pedroxs on 02/09/15.
 */
class DroolsJarConsumerSimulation extends Simulation {

  object Message {
    val message = exec(http("Message")
      .get("/message")
      .check(status.is(200)))

    val remoteMessage = exec(http("Remote Message")
      .get("/remote-message")
      .check(status.is(200)))
  }

  object SwitchVersion {
    val version1 = exec(http("Switch to Version 1")
      .get("/rule").queryParam("version", "1")
      .check(status.is(200)))

    val version2 = exec(http("Switch to Version 2")
      .get("/rule").queryParam("version", "2")
      .check(status.is(200)))
  }

  private val host = "172.17.0.100"

  val httpConf = http
    .baseURL("http://" + host + ":8080")
    .acceptHeader("*/*")
    .disableCaching

  val users = scenario("Users").exec(Message.message)
  val v1 = scenario("Admin V1").exec(SwitchVersion.version1)
  val v2 = scenario("Admin V2").exec(SwitchVersion.version2)

  setUp(
    users.inject(rampUsers(5000) over (5 minutes)),
    v1.inject(atOnceUsers(1)),
    v2.inject(nothingFor(60 seconds), atOnceUsers(1))
  ).protocols(httpConf)

}
