package com.statspade.kafkaspark

import com.statspade.Utilities.LoadProperties
import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.explode




object kafka_spark extends App {
val logfilename = LoadProperties.prop.getProperty("twitter_topic")+".log";
		System.setProperty("logfile", logfilename); 
val logger = LoggerFactory.getLogger(kafka_spark.getClass)

val kafkaParams = Map[String, Object](
    
  "bootstrap.servers" -> LoadProperties.prop.getProperty("Kafka_broker"),
  "key.deserializer" -> classOf[StringDeserializer],
  "value.deserializer" -> classOf[StringDeserializer],
  "group.id" -> LoadProperties.prop.getProperty("Group_id"),
  "auto.offset.reset" -> LoadProperties.prop.getProperty("offset"),
  "enable.auto.commit" -> (false: java.lang.Boolean)
)
logger.info(kafkaParams.toString())
  val spark = SparkSession.builder.getOrCreate()
//val conf = new SparkConf()
//val conf = new SparkConf().setMaster("Local[*]").setAppName("APP")
val ssc = new StreamingContext(spark.sparkContext, Seconds(1))

val topics = Array(LoadProperties.prop.getProperty("consumer_topics"))
val stream = KafkaUtils.createDirectStream[String, String](
  ssc,
  org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent,
  org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe[String, String](topics, kafkaParams)
)

  

val rec=stream.map(record => ( record.value))
rec.foreachRDD(x=>{
  
  val new_rec=x.filter(y=>y.contains("id"))
 
  val a = spark.read.json(new_rec)
try
  {
val req_dat=a.select("user.id","user.name","user.location","user.followers_count","user.friends_count","user.listed_count","user.favourites_count","user.statuses_count","user.created_at","user.lang")
req_dat.write.mode("append")
  .format("com.databricks.spark.csv")
  .option("header", "false")
  .save(LoadProperties.prop.getProperty("hdfs-path"))
  }
  catch
  {
    case e:Exception => logger.error("Data Error:", e.getMessage+a.collect())

  
}})

ssc.start()
ssc.awaitTermination()

}
