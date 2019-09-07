import java.time.Duration
import java.util.Properties

import com.redis.{RedisClient, RedisClientPool}
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala._
import org.apache.kafka.streams.scala.kstream._
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}

object MainKStream extends App {


  import Serdes._

  val redisClientPool = new RedisClientPool("127.0.0.1", 7001)

  redisClientPool.withClient(_.set("A", "one"))
  redisClientPool.withClient(_.set("B", "two"))


  val p = new Properties()
  Map(
    StreamsConfig.NUM_STREAM_THREADS_CONFIG -> "6",
    StreamsConfig.BOOTSTRAP_SERVERS_CONFIG -> "0.0.0.0:9092",
    StreamsConfig.APPLICATION_ID_CONFIG -> "test"
  ).foreach { case (k, v) => p.put(k, v) }

  val inputTopic = "input"
  val outputTopic = "output"

  val builder: StreamsBuilder = new StreamsBuilder

  val inputStream: KStream[String, String] = builder.stream[String, String](inputTopic)

  val outputStream = inputStream
    .mapValues { str =>
      s"$str=${redisClientPool.withClient(_.get[String](str))}"
    }

  outputStream.to(outputTopic)


  val streams: KafkaStreams = new KafkaStreams(builder.build(), p)
  streams.start()
}
