package io.keepcoding.spark.sql.exercise2

import java.nio.file.Files

import org.apache.spark.sql.SparkSession

object SparkSqlBasics {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder()
      .master("local[*]")
      .appName("Spark SQL KeepCoding Base")
      .getOrCreate()

    val csvFilePath = "/Users/abueno/Documents/Github/BD - Bootcamp/big-data-processing-master/spark/spark-sql/spark_sql_base_project/src/main/resources/sample.csv" //getClass.getClassLoader.getResource("sample.csv").getFile
    val jsonFilePath = "/Users/abueno/Documents/Github/BD - Bootcamp/big-data-processing-master/spark/spark-sql/spark_sql_base_project/src/main/resources/sample.json" //getClass.getClassLoader.getResource("sample.json").getFile

    import spark.implicits._
    import org.apache.spark.sql.functions._

    // 1. Leemos ambos datasources cada uno con su formato correspondiente.
    val csvDF = spark
      .read
      .option("header", "true") //Primera linea como header
      .option("inferSchema", "true") //Inferir los tipos de datos
      .csv(csvFilePath)
    val jsonDF = spark
      .read
      .json(jsonFilePath)

    // 2. Unificamos los datos CSV & JSON, usando la función unionByName(...)
    val fullDF = csvDF.unionByName(jsonDF)
    //fullDF.show(truncate = false)

    // 3. ¿Qué alumno tiene mejor nota media del instituto?
    fullDF
      .select($"nombre", $"apellido", $"nota")
      .groupBy($"nombre", $"apellido")
      .agg(avg($"nota").as("nota_media"))
      .sort($"nota_media")
      .limit(1)
      .show()

    // 4. ¿Qué clase tiene las peores notas?
    fullDF
      .select($"clase", $"nota")
      .groupBy($"clase")
      .agg(avg($"nota").as("nota_media"))
      .sort($"nota_media".asc)
      .limit(1)
      .show()

    // 5. Añade el tipo de curso a cada alumno 'ciencias'/'humanidades'
    //    dentro de la columna 'curso', escribe los resultados en formato JSON
    //    en un directorio temporal:
    //
    //    val outputDir = s"""${Files.createTempDirectory("spark").toFile.getAbsolutePath}/results"""

    val outputDir = s"""${Files.createTempDirectory("spark").toFile.getAbsolutePath}/results"""


    println(s"Result write into $outputDir")
    spark.close()
  }

}
