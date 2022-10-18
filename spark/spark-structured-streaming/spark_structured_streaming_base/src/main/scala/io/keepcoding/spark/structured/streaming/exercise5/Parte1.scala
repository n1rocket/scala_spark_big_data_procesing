package io.keepcoding.spark.structured.streaming.exercise5

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.from_json
import org.apache.spark.sql.types.{StringType, StructField, StructType}


object Parte1 {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder()
      .master("local[*]")
      .appName("Spark Structured Streaming KeepCoding Base")
      .getOrCreate()

    val kafkaDF = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "34.125.59.118:9092")
      .option("subscribe", "test")
      .load()

    //kafkaDF.printSchema()
    import spark.implicits._

    val jsonSchema = StructType(Seq(
      StructField("id", StringType, nullable = false),
      StructField("name", StringType, nullable = false)
    ))

    val transformDF = kafkaDF
      .select(from_json($"value".cast(StringType), jsonSchema).as("json"))
      .select("json.*")

    transformDF
      .writeStream
      .format("console")
      .start()
      .awaitTermination()

    spark.close()
  }

}
