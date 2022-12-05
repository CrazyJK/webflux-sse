package learn.notify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NotifyMvcApplication {

  public static void main(String[] args) {
    SpringApplication.run(NotifyMvcApplication.class, args);
  }

  @Bean
  HttpExchangeRepository httpTraceRepository() {
    return new InMemoryHttpExchangeRepository();
  }

}
