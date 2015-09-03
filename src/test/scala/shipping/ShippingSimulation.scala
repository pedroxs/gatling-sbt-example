package shipping

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

/**
  * Created by Pedro on 14/07/2015.
  */
class ShippingSimulation extends Simulation {

  val feeder = csv("calculate.csv").random

  object Calculate {
     val calculate = feed(feeder)
       .exec(http("Calculate")
       .get("/shipping/calculate")
       .queryParamMap(Map("zip" -> "${zip}", "weight" -> "${weight}"))
       .check(status.is(200))
       .check(jsonPath("$..shippingCost").exists))
//       .check(jsonPath("$.._embedded.shippings[0].shippingCost").exists))
//       .check(jsonPath("$.._embedded.shippings[0].shippingCost").is("${cost}")))
   }

  object UpdateDB {
    val updateDB = exec(http("Update DB")
      .post("/shipping/bulk-insert")
      .body(StringBody(GenerateData.asJsonString(20000))).asJSON
      .check(status.is(201)))

    val upload = exec(http("Upload")
      .post("/shipping/upload")
      .formParam("name", "Tabela de Frete")
      .formUpload("file", "/home/pedroxs/Documents/Netshoes/Tabela_de_Frete_FIS.csv")
      .check(status.is(201)))
  }

//  private val host = "192.168.59.103"
  private val host = "localhost"

  val httpConf = http
     .baseURL("http://" + host + ":8080")
     .acceptHeader("*/*")
     .disableCaching
     .contentTypeHeader("application/json")

  val uploadHttpConf = http
    .baseURL("http://" + host + ":8081")
    .acceptHeader("*/*")
    .disableCaching
    .contentTypeHeader("application/json")

  val users = scenario("Users").exec(Calculate.calculate)
  val admin = scenario("Admin").exec(UpdateDB.upload)

   setUp(
     users.inject(rampUsers(2500) over (5 minutes)).protocols(httpConf),
     admin.inject(nothingFor(60 seconds), atOnceUsers(1)).protocols(uploadHttpConf)
   )//.protocols(httpConf)
 }
