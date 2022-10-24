package io.keepcoding.spark.exercise.streaming

import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType, TimestampType}
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object AntennaStreamingJob extends StreamingJob {

  override val spark: SparkSession = SparkSession
    .builder()
    .master("local[20]")
    .appName("Final Exercise SQL Streaming")
    .getOrCreate()

  import spark.implicits._

  override def readFromKafka(kafkaServer: String, topic: String): DataFrame = {
    spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", kafkaServer)
      .option("subscribe", topic)
      .load()
  }

  override def parserJsonData(dataFrame: DataFrame): DataFrame = {
    //El Struct será otra info

    val jsonSchema = StructType(Seq(
      StructField("timestamp", TimestampType, nullable = false),
      StructField("id", StringType, nullable = false),
      StructField("metric", StringType, nullable = false),
      StructField("value", IntegerType, nullable = false),
    ))

    dataFrame
      .select(from_json($"value".cast(StringType), jsonSchema).as("json"))
      .select("json.*")

  }


  override def readAntennaMetadata(jdbcURI: String, jdbcTable: String, user: String, password: String): DataFrame = {
    spark
      .read
      .format("jdbc")
      .option("url", jdbcURI)
      .option("dbtable", jdbcTable)
      .option("user", user)
      .option("password", password)
      .load()
  }

  override def enrichAntennaWithMetadata(antennaDF: DataFrame, metadataDF: DataFrame): DataFrame = {
    antennaDF.as("a")
      .join(
        metadataDF.as("b"),
        $"a.id" === $"b.id"
      )
  }

  override def computeDevicesCountByCoordinates(dataFrame: DataFrame): DataFrame = {
    //Esto cambiará en el proyecto final
    dataFrame
      .filter($"metric" === "devices_count")
      .select($"timestamp", $"location", $"value")
      .withWatermark("timestamp", "10 seconds") //1 minute
      .groupBy($"location", window($"timestamp", "30 seconds").as("window")) //5 minutes
      .agg(
        avg($"value").as("avg_devices_count"), //mismo nombre que la tabla sql donde vamos a guardar (revisar provisioner)
        max($"value").as("max_devices_count"),
        min($"value").as("min_devices_count"),
      )
      .select($"location", $"window.start".as("date"), $"avg_devices_count", $"max_devices_count", $"min_devices_count")

  }

  //Para que funcionen los futuros

  import scala.concurrent.ExecutionContext.Implicits.global

  override def writeToJdbc(dataFrame: DataFrame, jdbcURI: String, jdbcTable: String, user: String, password: String): Future[Unit] = Future {
    dataFrame
      .writeStream
      .foreachBatch {
        (batchDF: DataFrame, idPage: Long) => {
          batchDF
            .write
            .mode(SaveMode.Append)
            .format("jdbc")
            .option("driver", "org.postgresql.Driver")
            .option("url", jdbcURI)
            .option("dbtable", jdbcTable)
            .option("user", user)
            .option("password", password)
            .save()
        }
      }
      .start()
      .awaitTermination()
  }

  override def writeToStorage(dataFrame: DataFrame, storageRootPath: String): Future[Unit] = Future {
    dataFrame
      .select(
        $"timestamp", $"id", $"metric", $"value",
        year($"timestamp").as("year"),
        month($"timestamp").as("month"),
        dayofmonth($"timestamp").as("day"),
        hour($"timestamp").as("hour"),
      )
      .writeStream
      .format("parquet")
      .option("path", s"$storageRootPath/data")
      .option("checkpointLocation", s"$storageRootPath/checkpoint")
      .partitionBy("year", "month", "day", "hour")
      .start
      .awaitTermination()
  }

  def main(args: Array[String]): Unit = {
    //    val Array(kafkaServer, topic, jdbcUri, jdbcMetadataTable, aggJdbcTable, jdbcUser, jdbcPassword, storagePath) = args
    //run(args)

    val storage = "/Users/abueno"
    val ipKafka = "34.125.228.143:9092"
    val jdbcUri = s"jdbc:postgresql://34.173.106.255:5432/postgres"
    val jdbcUser = "postgres"
    val jdbcPassword = "keepcoding"

    val kafkaDF = readFromKafka(ipKafka, "antenna_telemetry")
    val antennaDF = parserJsonData(kafkaDF)
    val storageFuture = writeToStorage(antennaDF, s"$storage/tmp/antenna_parquet/")
    val metadataDF = readAntennaMetadata(jdbcUri, "metadata", jdbcUser, jdbcPassword)

    val enrichedDF = enrichAntennaWithMetadata(antennaDF, metadataDF)
    val countByLocationComputedAggsDF = computeDevicesCountByCoordinates(enrichedDF)
    val jdbcFuture = writeToJdbc(countByLocationComputedAggsDF, jdbcUri, "antenna_agg", jdbcUser, jdbcPassword)

    //spark.streams.awaitAnyTermination() //Para no usar futuros //Se eliminan los await de los futuros
    // y se pone esta linea, no es necesario el await result

    Await.result(Future.sequence(Seq(storageFuture, jdbcFuture)), Duration.Inf)

    //    countByLocationComputedAggsDF
    //      .writeStream
    //      .format("console")
    //      .start()
    //      .awaitTermination()

    spark.close()

  }
}
