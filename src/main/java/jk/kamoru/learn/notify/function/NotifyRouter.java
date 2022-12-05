package jk.kamoru.learn.notify.function;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration(proxyBeanMethods = false)
public class NotifyRouter {

  @Bean
  public RouterFunction<ServerResponse> route(NotifyHandler notifyHandler) {
    return RouterFunctions
        .route(POST("/noti"), notifyHandler::receive)
        .andRoute(GET("/noti/sse/{id}"), notifyHandler::sse);
  }

  @Bean
  CorsWebFilter corsWebFilter() {
    CorsConfiguration corsConfig = new CorsConfiguration();
    corsConfig.setAllowedOrigins(Arrays.asList("http://allowed-origin.com"));
    corsConfig.setMaxAge(8000L);
    corsConfig.addAllowedMethod("PUT");
    corsConfig.addAllowedHeader("Baeldung-Allowed");

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfig);

    return new CorsWebFilter(source);
  }

}
