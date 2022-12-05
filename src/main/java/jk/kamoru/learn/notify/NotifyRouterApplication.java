package jk.kamoru.learn.notify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NotifyRouterApplication {

  public static void main(String[] args) {
    SpringApplication.run(NotifyRouterApplication.class, args);
  }
}
