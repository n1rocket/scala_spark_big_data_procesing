package io.keepcoding.spark.structured.streaming.exercise5

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{from_json, lit}
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.types.{StringType, StructField, StructType}


object Parte5 {
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

    // id, name => topic, key(id), value(name)
    val transformDF = kafkaDF
      .select(from_json($"value".cast(StringType), jsonSchema).as("json"))
      .select("json.*")
      .select(lit("test1").as("topic"), $"id".as("key"), $"name".as("value"))

    transformDF
      .writeStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "34.125.59.118:9092")
      .option("checkpointLocation", "/tmp/parte4:checkpoint")
      .trigger(Trigger.Continuous("1 seconds"))
      .start()
      .awaitTermination()

    spark.close()
  }

}
