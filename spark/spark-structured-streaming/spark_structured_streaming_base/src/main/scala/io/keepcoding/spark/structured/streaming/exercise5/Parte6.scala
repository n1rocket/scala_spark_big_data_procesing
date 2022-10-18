package io.keepcoding.spark.structured.streaming.exercise5

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{from_json, lit, struct, to_json}
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.types.{BinaryType, IntegerType, StringType, StructField, StructType, TimestampType}


object Parte6 {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder()
      .master("local[*]")
      .appName("Spark Structured Streaming KeepCoding Base")
      .getOrCreate()

    import spark.implicits._

    val jsonSchema = StructType(Seq(
      StructField("sensor_id", IntegerType, nullable = false),
      StructField("temperature", IntegerType, nullable = false),
      StructField("humidity", IntegerType, nullable = false),
      StructField("timestamp", TimestampType, nullable = false)
    ))

    val kafkaDF = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "34.125.59.118:9092")
      .option("subscribe", "test")
      .load()

    //kafkaDF.dropDuplicates("guid") //Eliminar duplicados
    //with watermark para descartar

    val staticDF = spark
      .read
      .format("csv")
      .option("header", "true")
      .option("delimiter", "|")
      .load("/Users/abueno/Documents/Github/BD - Bootcamp/big-data-processing-master/spark/spark-structured-streaming/spark_structured_streaming_base/src/main/resources/exercise3/stream-batch/sensor_metadata.csv")

    // id, name => topic, key(id), value(name)
    val transformDF = kafkaDF
      .select(from_json($"value".cast(StringType), jsonSchema).as("json"))
      .select("json.*")
      //.select(lit("test1").as("topic"), $"id".as("key"), $"name".as("value"))

    val joinDF = transformDF.as("a")
      .join(staticDF.as("b"), $"a.sensor_id" === $"b.sensor_id")
      .select(
        lit("test1").as("topic"),
        $"a.sensor_id".cast(StringType).as("key"),
        to_json(struct($"a.sensor_id", $"a.temperature", $"a.humidity", $"a.timestamp", $"b.maintainer"))
          .as("value")
      )
      .select($"topic", $"key", $"value")


    joinDF.printSchema()

/*    joinDF
      .writeStream
      .format("console")
      .start()
      .awaitTermination()*/
    joinDF
      .writeStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "34.125.59.118:9092")
      .option("checkpointLocation", "/tmp/parte6:checkpoint")
      .start()
      .awaitTermination()

    spark.close()
  }

}
