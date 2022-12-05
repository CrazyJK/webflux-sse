package learn.notify.function;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import learn.notify.domain.Notify;
import learn.notify.domain.NotifyChannel;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks.Many;

@Slf4j
@Component
public class NotifyHandler {

  @Autowired
  private NotifyChannel notifyChannel;

  public Mono<ServerResponse> receive(ServerRequest request) {
    return request.bodyToMono(Notify.class)
        .doOnNext((n) -> {
          log.debug(">> receive: {}", n);

          final String id = n.getTo();
          if (id == null || id.trim().length() == 0) {
            throw new RuntimeException("to is blank");
          }
          n.setId(notifyChannel.seqNext());

          final Many<Notify> sink = notifyChannel.findSink(id);
          if (sink == null) {
            log.warn("id [{}] is not connected", id);
          } else {
            sink.tryEmitNext(n);
          }
        })
        .flatMap((n) -> {
          return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(n), Notify.class);
        });
  }

  public Mono<ServerResponse> sse(ServerRequest request) {
    final String id = request.pathVariable("id");
    final Many<Notify> sink = notifyChannel.getSink(id);

    log.debug(">> accept {} currentSubscriberCount {}", id, sink.currentSubscriberCount());

    return ServerResponse.ok().contentType(MediaType.TEXT_EVENT_STREAM)
        .body(
            BodyInserters.fromServerSentEvents(
                sink.asFlux()
                    .map(n -> ServerSentEvent.builder(n)
                        .id(String.valueOf(n.getId()))
                        .event("Notify")
                        .comment("comment")
                        .build())
                    .doOnCancel(() -> {
                      sink.asFlux().blockLast(Duration.ofSeconds(10));
                    })));
  }

}
