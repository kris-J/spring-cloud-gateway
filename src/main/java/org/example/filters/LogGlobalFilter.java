package org.example.filters;

import com.jd.jr.merchant.login.interceptor.JrAccountInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.AbstractServerHttpRequest;
import org.springframework.http.server.reactive.AbstractServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        ServerHttpRequest request = exchange.getRequest();
        AbstractServerHttpRequest request1 = (AbstractServerHttpRequest) request;
        Object nativeRequest = request1.getNativeRequest();

        ServerHttpResponse response = exchange.getResponse();
        AbstractServerHttpResponse response1 = (AbstractServerHttpResponse) response;
        Object nativeResponse = response1.getNativeResponse();

        JrAccountInterceptor jrAccountInterceptor = new JrAccountInterceptor();
        try {
            boolean b = jrAccountInterceptor.preHandle((HttpServletRequest) nativeRequest, (HttpServletResponse) nativeResponse, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
