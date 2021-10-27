package org.example;

import org.example.filters.JsfGatewayFilterFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

import static com.jd.jsf.gd.msg.ResponseListenersCaller.build;

/**
 * description: Application
 * date: 2021/10/8 9:55
 * author: fangjie24
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

//    @Bean
//    public JsfGatewayFilterFactory addJsfGatewayFilterFactory() {
//        return new JsfGatewayFilterFactory();
//    }

//    @Bean
//    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
//        return builder.routes().route(r ->
//                r.path("/index/index4")
//                .filters(f ->
//                        f.modifyRequestBody(String.class, Hello.class, MediaType.APPLICATION_JSON_VALUE,(exchange, s) -> {return Mono.just(new Hello(s.toUpperCase()));})
//                        .modifyResponseBody(String.class, Hello.class, MediaType.APPLICATION_JSON_VALUE,(exchange, s) -> {return Mono.just(new Hello(s.toUpperCase()));})
//                        .addRequestParameter("color", "RED")
//                        .addResponseHeader("X-Response-Red","Blue")
//                )
//                .uri("http://localhost:8080")
//        ).build();
//    }


    }
