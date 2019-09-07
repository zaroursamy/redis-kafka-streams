name := "kafka-streams"

version := "0.1"

scalaVersion := "2.12.9"

val kafkaV = "2.2.1"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-streams" % kafkaV,
  "org.apache.kafka" % "kafka-clients" % kafkaV,
  "org.apache.kafka" %% "kafka-streams-scala" % kafkaV,
  "net.debasishg" %% "redisclient" % "3.10"
)