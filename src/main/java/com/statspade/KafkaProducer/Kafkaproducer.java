package com.statspade.KafkaProducer;
import java.util.Properties;


import com.statspade.Utilities.LoadProperties;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class Kafkaproducer {
	static Properties props = new Properties();
	static Producer<String, String> producer = null;
	public void set_property()
	{
		    props.put("bootstrap.servers", LoadProperties.prop().getProperty("Kafka_broker"));
		    //props.put("bootstrap.servers", "10.100.105.52:6667");
		    props.put("acks", "all");
		    props.put("retries", 0);
		    props.put("batch.size", 16384);
		    props.put("linger.ms", LoadProperties.prop().getProperty("linger_ms"));
		    props.put("buffer.memory", 33554432);
		    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		    
	}
	public void kafkaconnect()
	{
		try{
			set_property();
			producer = new KafkaProducer<>(props);
		
		
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
				
	}
	
	public void send_message(String Message)
	{
		  try {
			       
		       		producer.send(new ProducerRecord<String, String>(LoadProperties.prop().getProperty("kafka_topics"), Message), new Callback() {
				
				@Override
				public void onCompletion(RecordMetadata metadata, Exception exception) {
					if(exception != null) {
						exception.printStackTrace();
                     } else {
                        System.out.println("The offset of the record we just sent is: " + metadata.offset());
                     }
					
				}
			});
		  } catch (Exception e) {
		      e.getMessage();

		    } 
		  //System.out.println("connectin3");
	}
	public void close()
	{   	
		      producer.close();	    
	}
}
