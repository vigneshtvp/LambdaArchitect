package com.statspade.KafkaProducer;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

import com.statspade.Utilities.LoadProperties;

public class TwitterAuth {
	

 public static void run(String consumerKey, String consumerSecret, String token, String secret,String topic ) throws Exception {
	 
	   // Source.KafkaProducer kp= new Source.KafkaProducer();
	    BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);
        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint(false);
        endpoint.trackTerms(Lists.newArrayList("twitterapi", "#"+topic));
        Authentication auth = new OAuth1(consumerKey, consumerSecret, token, secret);
        Client client = new ClientBuilder()
            .hosts(Constants.STREAM_HOST)
            .endpoint(endpoint)
            .authentication(auth)
            .processor(new StringDelimitedProcessor(queue))
            .build();
        client.connect(); 
        Kafkaproducer kp=new Kafkaproducer();
        kp.kafkaconnect();
       

while(true)
{
      String msg = queue.take();
      System.out.println(msg);
      kp.send_message(msg);
	


 }	
  }
 


  public static void main(String[] args) throws Exception {
		String logfilename = LoadProperties.prop().getProperty("twitter_topic").trim()+"_"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm"))+".log";
		System.setProperty("logfile", logfilename);
 Logger LOG = LoggerFactory.getLogger(TwitterAuth.class);
	  
	 String Consumer_key=LoadProperties.prop().getProperty("Consumer_key");
	 String Consumer_token=LoadProperties.prop().getProperty("Consumer_token");
	 String token=LoadProperties.prop().getProperty("token");
	 String secret=LoadProperties.prop().getProperty("secret");
	 String topic=LoadProperties.prop().getProperty("twitter_topic");
	
    try 
    {
      TwitterAuth.run(Consumer_key,Consumer_token,token,secret,topic);
    }
    catch (InterruptedException e)
    {
      System.out.println(e);
    }
}
}

