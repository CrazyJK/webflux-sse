package learn.webmvc;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import learn.webmvc.domain.Notification;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Slf4j
@RestController
public class NotifyController {

  // Sinks.Many<Notification> sink = Sinks.many().replay().all();
  Sinks.Many<Notification> sink = Sinks.many().multicast().onBackpressureBuffer();

  AtomicInteger eventId = new AtomicInteger(0);

  int i;

  @CrossOrigin("*")
  @GetMapping("/noti/sse")
  public Flux<ServerSentEvent<Notification>> noti() {
    log.info("currentSubscriberCount {}", sink.currentSubscriberCount());
    return sink.asFlux().map(n -> ServerSentEvent.builder(n).id(String.valueOf(n.getId())).event("Notify").comment("comment").build()).doOnCancel(() -> sink.asFlux().blockLast(Duration.ofSeconds(10)));
  }

  @PostMapping("/noti")
  public Mono<Notification> receive() {
    Notification noti = Notification.builder().when(System.currentTimeMillis()).message("noti " + ++i).id(eventId.incrementAndGet()).build();
    log.info("receive {}", noti);
    log.info("currentSubscriberCount {}", sink.currentSubscriberCount());
    return Mono.just(noti).doOnNext(n -> sink.tryEmitNext(n));
  }

}
