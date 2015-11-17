package pers.ly.demos.springframework.xmlconf.app;

import java.util.Properties;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import pers.ly.demos.springframework.xmlconf.Utils;


public class StaticApp {
	public static void main(String[] args) {
	      ApplicationContext context = 
	          new ClassPathXmlApplicationContext("spring/applicationContext.xml");
	      Properties databaseConfig = Utils.getDatabaseConfig();
	      System.out.println(databaseConfig.get("database.url"));
	      
	      String projectName = (String)context.getBean("projectName");
	      System.out.println(projectName);
	  }
}
