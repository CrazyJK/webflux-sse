package learn.webmvc;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks.Many;

@RequiredArgsConstructor
@Slf4j
@RestController
public class NotifyController {

  private final NotifyChannel notifyChannel;

  private AtomicInteger eventId = new AtomicInteger(0);

  
  @CrossOrigin("*")
  @GetMapping("/noti/sse/{id}")
  public Flux<ServerSentEvent<Notify>> noti(@PathVariable String id) {
    final Many<Notify> sink = notifyChannel.getSink(id);

    log.debug("accept {} currentSubscriberCount {}", id, sink.currentSubscriberCount());

    return sink.asFlux()
        .map(n -> ServerSentEvent.builder(n)
            .id(String.valueOf(n.getId()))
            .event("Notify")
            .comment("comment")
            .build())
        .doOnCancel(() -> {
          sink.asFlux().blockLast(Duration.ofSeconds(10));
        });
  }

  @PostMapping("/noti")
  public Mono<Notify> receive(@RequestBody Notify notify) {
    log.debug("received {}", notify);
    
    final String id = notify.getTo();
    if (id == null || id.trim().length() == 0) {
      throw new RuntimeException("to is blank");
    }
    
    notify.setId(eventId.incrementAndGet());
    
    final Many<Notify> sink = notifyChannel.findSink(id);
    if (sink == null) {
      log.warn("id {} is not connected", id);
      return Mono.just(notify);
    }
    
    return Mono.just(notify).doOnNext(n -> sink.tryEmitNext(n));
  }
  
}
