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
    val feeder = (1 to 10000).map(i => Map("number" -> i))

    val nextVersion = feed(feeder)
      .exec(http("Next Version #${number}")
        .get("/rule").queryParam("version", "${number}")
        .check(status.is(200)))
  }


  private val host = System.getProperty("hostUrl", "localhost")

  val httpConf = http
    .baseURL("http://" + host + ":8080")
    .acceptHeader("*/*")
    .disableCaching

  val users = scenario("Users").exec(Message.message)
  val admin = scenario("Admin").exec(SwitchVersion.nextVersion)

  val rampUser = Integer.getInteger("users", 1)
  val rampAdmin = Integer.getInteger("admins", 1)
  val timeDuration = Integer.getInteger("duration", 1)
  val timeUnit = System.getProperty("time", "seconds") match {
    case "seconds" => DurationInteger(timeDuration).seconds
    case "minutes" => DurationInteger(timeDuration).minutes
    case "hours" => DurationInteger(timeDuration).hours
    case _ => DurationInteger(timeDuration).seconds
  }

  setUp(
    users.inject(rampUsers(rampUser) over timeUnit),
    admin.inject(rampUsers(rampAdmin) over timeUnit)
  ).protocols(httpConf)

}
