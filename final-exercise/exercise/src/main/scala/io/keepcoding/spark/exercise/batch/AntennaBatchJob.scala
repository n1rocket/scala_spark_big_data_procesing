package io.keepcoding.spark.exercise.batch

import io.keepcoding.spark.exercise.streaming.AntennaStreamingJob.{readFromKafka, spark}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

import java.time.OffsetDateTime
import scala.concurrent.Future

object AntennaBatchJob extends BatchJob {
  override val spark: SparkSession = SparkSession
    .builder()
    .master("local[20]")
    .appName("Final Exercise SQL Batch")
    .getOrCreate()

  import spark.implicits._

  override def readFromStorage(storagePath: String, filterDate: OffsetDateTime): DataFrame = {
    spark
      .read
      .format("parquet")
      .load(s"$storagePath/data")
      .filter(
        $"year" === filterDate.getYear &&
          $"month" === filterDate.getMonthValue &&
          $"day" === filterDate.getDayOfMonth &&
          $"hour" === filterDate.getHour
      )
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
      .drop($"b.id")
  }

  override def computeDevicesCountByCoordinates(dataFrame: DataFrame): DataFrame = {
    dataFrame
      .filter($"metric" === "devices_count")
      .select($"timestamp", $"location", $"value")
      //No necesario para Batch //.withWatermark("timestamp", "10 seconds") //1 minute
      .groupBy($"location", window($"timestamp", "1 hour").as("window")) //5 minutes
      .agg(
        avg($"value").as("avg_devices_count"), //mismo nombre que la tabla sql donde vamos a guardar (revisar provisioner)
        max($"value").as("max_devices_count"),
        min($"value").as("min_devices_count"),
      )
      .select($"location", $"window.start".as("date"), $"avg_devices_count", $"max_devices_count", $"min_devices_count")
  }

  override def computeErrorAntennaByModelAndVersion(dataFrame: DataFrame): DataFrame = ???

  override def computePercentStatusByID(dataFrame: DataFrame): DataFrame = ???

  override def writeToJdbc(dataFrame: DataFrame, jdbcURI: String, jdbcTable: String, user: String, password: String): Unit = {
    dataFrame
      .write
      .mode(SaveMode.Append)
      .format("jdbc")
      .option("url", jdbcURI)
      .option("dbtable", jdbcTable)
      .option("user", user)
      .option("password", password)
      .save()
  }

  override def writeToStorage(dataFrame: DataFrame, storageRootPath: String): Unit = {
    dataFrame
      .write
      .format("parquet")
      .partitionBy("year", "month", "day", "hour")
      .mode(SaveMode.Overwrite)
      .save(s"$storageRootPath/historical")
  }

  def main(args: Array[String]): Unit = {
    val offsetDateTime = OffsetDateTime.parse("2022-10-20T23:00:00Z")

    val antennaDF = readFromStorage("/tmp/antenna_parquet/", offsetDateTime)
    val metadataDF = readAntennaMetadata(s"jdbc:postgresql://34.173.106.255:5432/postgres",
      "antenna_agg",
      "postgres",
      "keepcoding"
    )
    val antennaMetadataDF = enrichAntennaWithMetadata(antennaDF, metadataDF).cache()
    val aggByCoordinatesDF = computeDevicesCountByCoordinates(antennaMetadataDF)
    //val aggPercentStatusDF = computePercentStatusByID(antennaMetadataDF)
    //val aggErroAntennaDF = computeErrorAntennaByModelAndVersion(antennaMetadataDF)

    writeToJdbc(aggByCoordinatesDF, s"jdbc:postgresql://34.173.106.255:5432/postgres",
      "antenna_1h_agg",
      "postgres",
      "keepcoding"
    )
    //writeToJdbc(aggPercentStatusDF, jdbcUri, aggJdbcPercentTable, jdbcUser, jdbcPassword)
    //writeToJdbc(aggErroAntennaDF, jdbcUri, aggJdbcErrorTable, jdbcUser, jdbcPassword)

    writeToStorage(antennaDF, "/tmp/antenna_parquet/")

    spark.close()

  }
}
