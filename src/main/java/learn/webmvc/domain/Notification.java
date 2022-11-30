package learn.webmvc.domain;

import java.util.Date;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Notification {

  @Builder.Default final Date date = new Date();
  final long when;
  final String type;
  final String from;
  final String to;
  final String message;

  final int number;

}
