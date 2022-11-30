package learn.webmvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class NotifyApp {

  public static void main(String[] args) {
    SpringApplication.run(NotifyApp.class, args);
  }

  @Bean
  HttpTraceRepository httpTraceRepository() {
    return new InMemoryHttpTraceRepository();
  }

}
