package shipping

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Paths, Files}

import com.fasterxml.jackson.databind.ObjectMapper
import io.gatling.core.util.FastByteArrayInputStream

import scala.io.Source
import scala.util.Random

/**
 * Created by Pedro on 15/07/2015.
 */
object GenerateData {

  case class Shipping(carrier: String, service: String, zipStart: Int, zipEnd: Int, weightStart: Double, weightEnd: Double, shippingCost: Double, deliveryTime: Int)
  val mapper = new ObjectMapper().findAndRegisterModules()

  object Defaults {
    val carrier = List("Correo Argentino", "Logistica Andreani", "Correo OCA", "Correo UNIR SA")
    val service = List("Normal", "Express")
    var zip = (1000, 1070)
    val weight = List((0.0, 10.0), (10.01, 30.0), (30.01, 100.00))
    val shippingCost = List(20.00, 38.00, 63.00)
    val deliveryTime = List(2, 3, 7)
    val zipIncrements = 50 to 120
    def nextTick() = {
      zip = (zip._2 + 1, zip._2 + zipIncrements(Random.nextInt(zipIncrements.length)))
    }
  }

  def generate(total: Int) = {
    (1 to total).map { i =>
      val result = Defaults.weight.map { weight =>
        Shipping(
          carrier = Random.shuffle(Defaults.carrier).head,
          service = Random.shuffle(Defaults.service).head,
          zipStart = Defaults.zip._1,
          zipEnd = Defaults.zip._2,
          weightStart = weight._1,
          weightEnd = weight._2,
          shippingCost = Random.shuffle(Defaults.shippingCost).head,
          deliveryTime = Random.shuffle(Defaults.deliveryTime).head
        )
      }
      Defaults.nextTick()
      result
    }
  }

  def asJsonString(total: Int): String = {
    val json = StringBuilder.newBuilder
    json ++= "["
    json ++= generate(total).flatten.map(mapper.writeValueAsString).mkString(", ")
    json ++= "]"
    json.toString()
  }

  def time[R](block: => R): R = {
    val t0 = System.nanoTime()
    val result = block    // call-by-name
    val t1 = System.nanoTime()
    println("Elapsed time: " + (t1 - t0) + "ns")
    result
  }

  def main(args: Array[String]) {
    println("generate the data:")
    val jsonString: String = time { asJsonString(20000) }

    println("write the file")
    time {
      Files.write(Paths.get("target/my-data.json"), jsonString.getBytes(StandardCharsets.UTF_8))
    }
    //    println(asJsonString(5))

    Source.createBufferedSource(Files.newInputStream(Paths.get(""))).getLines().sliding(500).map { lines =>
      lines.map(_.split(","))
    }
  }
}