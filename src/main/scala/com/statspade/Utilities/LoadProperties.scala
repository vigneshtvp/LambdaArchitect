package com.statspade.Utilities

import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


object LoadProperties {
 

  val a=10;
   var prop=new Properties();
  var inStream = new FileInputStream("config.properties");
  prop.load(inStream);

  
}