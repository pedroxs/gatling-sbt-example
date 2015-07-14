package shipping

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

/**
  * Created by Pedro on 14/07/2015.
  */
class CalculateSimulation extends Simulation {

  val feeder = csv("calculate.csv").random

  object Calculate {
     val calculate = feed(feeder)
       .exec(http("Calculate")
       .get("/shipping/search/calculate")
       .queryParamMap(Map("zip" -> "${zip}", "weight" -> "${weight}"))
       .check(status.is(200))
       .check(jsonPath("$.._embedded.shippings[0].shippingCost").is("${cost}")))
   }

   val httpConf = http
     .baseURL("http://192.168.59.103:8080")
     .acceptHeader("*/*")
     .disableCaching
     .contentTypeHeader("application/json")

   val users = scenario("Users").exec(Calculate.calculate)

   setUp(
     users.inject(rampUsers(2) over (10 seconds))
   ).protocols(httpConf)
 }
