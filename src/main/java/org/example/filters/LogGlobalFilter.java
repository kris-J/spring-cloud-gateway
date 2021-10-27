package org.example.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * description: JsfGlobalFilter
 * date: 2021/10/11 11:48
 * author: fangjie24
 */
@Component
public class LogGlobalFilter implements GlobalFilter, Ordered {


    private static final Logger LOGGER = LoggerFactory.getLogger(LogGlobalFilter.class);

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long start = System.currentTimeMillis();
        LOGGER.info("LogGlobalFilter-记录全局日志pre");
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                    long end = System.currentTimeMillis();
                    LOGGER.info("LogGlobalFilter-记录全局日志post,time={}", (end-start));

//                    DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
//                    LOGGER.info(exchange.getResponse().bufferFactory())
                }
        ));
    }
}
