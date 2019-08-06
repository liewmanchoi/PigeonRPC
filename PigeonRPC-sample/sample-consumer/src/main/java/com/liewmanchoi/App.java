package com.liewmanchoi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Hello world! */
@SpringBootApplication
public class App {
  public static void main(String[] args) {
    SpringApplication application = new SpringApplication(App.class);
    application.setWebApplicationType(WebApplicationType.NONE);
    application.run(args);
  }
}
