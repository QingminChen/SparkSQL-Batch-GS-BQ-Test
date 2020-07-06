package com.qingmin.testProject

import org.apache.spark.internal.Logging
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.avro._


object sparkSQLBQGSExample extends Logging {



  def main (args: Array[String]): Unit = {

    val projectIDStr = "pubsub-test-project-16951"
    System.setProperty("GOOGLE_CLOUD_PROJECT",projectIDStr)


    val credentialsJsonFilePathStr = "/Users/chenqingmin/Codes/IntelliJ_IDEA_Workspace/Spark-Batch-GS-BQ-Test/src/main/resources/AllServicesKey.json"
    //val credentialsJsonFilePathStr = "/home/testinggcpuser/AllServicesKey.json"
    //val spark = SparkSession.builder.getOrCreate()
    val spark = SparkSession.builder.master("local[4]").getOrCreate()

    /** the following the code block ,all of them didn't work, especially someone mentioned you can pass the hadoop related properties into spark by add prefix
     *  spark.hadoop.xxxxx   it didn't work
     *  you can only pass the hadoop configuration properties via this way:
     *  spark.sparkContext.hadoopConfiguration.set("google.cloud.auth.service.account.enable", "true")
     *
     * //System.setProperty("GCS_PROJECT_ID",projectIDStr)
     * //System.setProperty("GOOGLE_APPLICATION_CREDENTIALS",projectIDStr)
     * //System.setProperty("GOOGLE_APPLICATION_CREDENTIALS",credentialsJsonFilePathStr)
     * //val spark = SparkSession.builder.config("spark.jars.packages", "com.google.cloud.spark:spark-bigquery-with-dependencies-assembly_2.11:0.16.1").master("local[4]").getOrCreate()
     * //val spark = SparkSession.builder.config("spark.jars.packages", "com.google.cloud.spark:spark-bigquery-with-dependencies_2.11:0.16.2").getOrCreate()
     *
     * //import com.google.cloud.spark.bigquery._
     * //val df=spark.read.bigquery(projectIDStr+".BigQueryTestDataset.Users")
     * //spark.conf.set("temporaryGcsBucket","project-16951-bucket5-write-bigquery")
     * //spark.conf.set("spark.hadoop.google.cloud.auth.service.account.enable","true")
     * //spark.conf.set("spark.hadoop.google.cloud.auth.service.account.json.keyfile",credentialsJsonFilePathStr)
     * //spark.conf.set("spark.hadoop.fs.gs.project.id",projectIDStr)
     * //spark.sparkContext.hadoopConfiguration.set("fs.gs.project.id", projectIDStr)
     * //spark.conf.set("spark.project",projectIDStr)
     * //spark.conf.set("credentialsFile",credentialsJsonFilePathStr)
     * //spark.sparkContext.hadoopConfiguration.set("fs.AbstractFileSystem.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFS")
     * //spark.sparkContext.hadoopConfiguration.set("fs.gs.impl","com.google.cloud.hadoop.fs.gcs.GoogleHadoopFileSystem")
     * //spark.sparkContext.hadoopConfiguration.set("fs.gs.auth.service.account.enable", "true")
     * //spark.sparkContext.hadoopConfiguration.set("fs.gs.auth.service.account.json.keyfile", credentialsJsonFilePathStr)
     * //val df = spark.read.format("bigquery").option("credentialsFile", credentialsJsonFilePathStr).option("dataset","BigQueryTestDataset").option("table","Users").load()   //no used to set the option credentialsFile
     * usersFavoriteNumberDF.write.format("bigquery").option("credentialsFile", credentialsJsonFilePathStr).option("temporaryGcsBucket", "project-16951-bucket5-write-bigquery").option("project", projectIDStr).option("dataset", "BigQueryTestDataset").save("UsersFavoriteNumber") // no use to set credentialsFile
     *
     **/

    spark.sparkContext.hadoopConfiguration.set("google.cloud.auth.service.account.enable", "true")
    spark.sparkContext.hadoopConfiguration.set("google.cloud.auth.service.account.json.keyfile", credentialsJsonFilePathStr)

    val df = spark.read.format("bigquery").option("project",projectIDStr).option("dataset","BigQueryTestDataset").option("table","Users").load()

    //val df = spark.read.format("avro").load("gs://project-16951-bucket3-for-avro/users.avro")   //Read from GCS avro file
    df.createOrReplaceTempView("usersTempInMemory")
    val usersFavoriteNumberDF = spark.sql("SELECT favorite_number FROM usersTempInMemory")
    usersFavoriteNumberDF.show()
    usersFavoriteNumberDF.printSchema()
    logInfo("12234325346")

    usersFavoriteNumberDF.write.format("avro").save("gs://project-16951-bucket4-write-avro/users_favorite_number.avro")   // write into GCS
    usersFavoriteNumberDF.write.format("bigquery").option("temporaryGcsBucket","project-16951-bucket5-write-bigquery").option("project",projectIDStr).option("dataset","BigQueryTestDataset").save("UsersFavoriteNumber")  // write into Bigquery

  }

}
