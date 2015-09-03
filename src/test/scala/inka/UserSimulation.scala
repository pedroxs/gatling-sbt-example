package inka

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.collection.mutable.{Map => MMap}

/**
 * Created by Pedro on 17/07/2015.
 */
class UserSimulation extends Simulation {

  val apiUser = "ns-estoque"
  val apiPwd = "ns-estoque#789"
  val token = "5b667d90-4b14-448e-a5d7-2fd00f671272"

  object Login {
    val login = exec(http("Login")
      .post("/rest/security/oauth/token")
      .basicAuth(apiUser, apiPwd)
      .body(StringBody("grant_type=client_credentials&scope=read%20write"))
      .check(status.is(200))
      .check(jsonPath("$..access_token").exists.saveAs("token")))
      .exec(session => {
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> generated token: " + session("token").as[String])
        session
    })
  }

  object User {
    val user = exec(session => {
//        println("previously generated token: " + token)
        session.set("token", token)
      })
      .exec(http("User")
        .get("/rest/security/user")
        .header("Authorization", "Bearer ${token}")
        .check(status.is(200)))
  }

  val httpConf = http
    .baseURL("http://hmg-ws-gateway.ns2online.com.br")
    .acceptHeader("*/*")
    .disableCaching
    .contentTypeHeader("application/json")

  val users = scenario("Users").exec(User.user)
  val login = scenario("Login").exec(Login.login)

  setUp(
//    login.inject(atOnceUsers(1)),
    users.inject(/*nothingFor(1 seconds), */rampUsers(1000) over (1 seconds))
  ).protocols(httpConf)

}
