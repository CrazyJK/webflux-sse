package learn.webmvc;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;

@Component
public class NotifyChannel {

  private final Map<String, Many<Notify>> notifyEvents = new HashMap<>();

  public Many<Notify> getSink(String id) {
    if (!notifyEvents.containsKey(id)) {
      notifyEvents.put(id, makeMany());
    }
    return findSink(id);
  }
  
  public Many<Notify> findSink(String id) {
    return notifyEvents.get(id);
  }

  private Many<Notify> makeMany() {
    return Sinks.many().multicast().onBackpressureBuffer();
  }

}
