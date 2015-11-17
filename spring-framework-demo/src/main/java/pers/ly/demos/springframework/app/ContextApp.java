package pers.ly.demos.springframework.app;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import pers.ly.demos.springframework.context.client.MessagePrinter;
import pers.ly.demos.springframework.context.service.IMessageService;

@Configuration
@ComponentScan("pers.ly.demos.springframework")
public class ContextApp {
	@Bean
    IMessageService mockMessageService() {
        return new IMessageService() {
            public String getMessage() {
              return "Hello World!";
            }
        };
    }

  public static void main(String[] args) {
      ApplicationContext context = 
          new AnnotationConfigApplicationContext(ContextApp.class);
      MessagePrinter printer = context.getBean(MessagePrinter.class);
      printer.printMessage();
  }
}
