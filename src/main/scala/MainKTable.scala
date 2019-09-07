import java.util.Properties
import java.util.concurrent.TimeUnit

import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{KStream, KTable}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}

object MainKTable extends App {

  import org.apache.kafka.streams.scala.ImplicitConversions._
  import org.apache.kafka.streams.scala.Serdes._


  val props = new Properties
  props.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "kafkastreams2")

  val builder = new StreamsBuilder

  val table: KTable[String, String] = builder
    .stream[String, String]("input")
    .selectKey { case (_, v) => v.split(":").headOption.getOrElse("") }
    .filter { case (k, _) => k.nonEmpty }
    .mapValues { str =>
      val Array(_, age) = str.split(":")
      age
    }
    .groupByKey
    .reduce { case (age1, age2) => s"$age1,$age2" }

  table.toStream.to("output2")

  val streams = new KafkaStreams(builder.build(), props)

  streams.start()


}
