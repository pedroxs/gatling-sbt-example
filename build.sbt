name := "gatling"

version := "1.0"

scalaVersion := "2.11.7"

enablePlugins(GatlingPlugin)

libraryDependencies ++= Seq(
  "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.1.6" % "test",
  "io.gatling"            % "gatling-test-framework"    % "2.1.6" % "test",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.5.2" % "test"
)

