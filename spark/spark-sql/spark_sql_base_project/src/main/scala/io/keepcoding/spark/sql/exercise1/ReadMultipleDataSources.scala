package io.keepcoding.spark.sql.exercise1

import org.apache.spark.sql.SparkSession

object ReadMultipleDataSources {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder()
      .master("local[*]")
      .appName("Spark SQL KeepCoding Base")
      .getOrCreate()

    // Read CSV
    // Con RDD
    //spark.sparkContext.textFile("/Users/abueno/Documents/Github/BD - Bootcamp/big-data-processing-master/spark/spark-sql/spark_sql_base_project/src/main/resources/sample.csv")
    //  .foreach(println)

    val inputFile = "/Users/abueno/Documents/Github/BD - Bootcamp/big-data-processing-master/spark/spark-sql/spark_sql_base_project/src/main/resources/sample.csv"
    val textDF = spark
      .read
      .option("header", "true") //Primera linea como header
      .option("inferSchema", "true") //Inferir los tipos de datos
      .csv(inputFile)

    textDF.show()
    textDF.printSchema()

    // Read JSON
    val inputFileJson = "/Users/abueno/Documents/Github/BD - Bootcamp/big-data-processing-master/spark/spark-sql/spark_sql_base_project/src/main/resources/sample.json"
    val textDF2 = spark
      .read
      .json(inputFileJson)

    textDF2.show()
    textDF2.printSchema()

    spark.close()
  }

}
