package learn.webmvc;

import org.springframework.http.codec.ServerSentEvent;
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

  int i;

  @GetMapping("/noti/see")
  public Flux<ServerSentEvent<Notification>> noti() {
    log.info("currentSubscriberCount {}", sink.currentSubscriberCount());
    return sink.asFlux().map(n -> ServerSentEvent.builder(n).build()).doOnCancel(() -> sink.asFlux().blockLast());
  }

  @PostMapping("/noti")
  public Mono<Notification> receive() {
    Notification noti = Notification.builder()
        .when(System.currentTimeMillis())
        .message("noti " + ++i)
        .number(sink.currentSubscriberCount()).build();
    log.info("receive {}", noti);
    return Mono.just(noti).doOnNext(n -> sink.tryEmitNext(n));
  }

}
