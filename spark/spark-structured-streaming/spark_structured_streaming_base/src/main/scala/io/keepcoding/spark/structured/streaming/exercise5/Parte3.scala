package io.keepcoding.spark.structured.streaming.exercise5

import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, SparkSession}

object Parte3 {


  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder()
      .master("local[*]")
      .appName("Spark Structured Streaming KeepCoding Base")
      .getOrCreate()

    import spark.implicits._

    val transformDF = spark
      .read
      .format("kafka")
      .option("kafka.bootstrap.servers", "34.125.59.118:9092")
      .option("subscribe", "test")
      .option("startingOffsets", "earliest")
      .option("endingOffsets", "latest")
      .load()

    transformDF.select($"value".cast(StringType))
      .show()

    spark.close()
  }

}
