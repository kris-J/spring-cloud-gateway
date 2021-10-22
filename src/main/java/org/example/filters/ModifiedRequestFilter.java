package org.example.filters;

import org.example.config.ModifiedRequestDecorator;
import org.example.config.RewriteConfig;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * description: ModifiedRequestFilter
 * date: 2021/10/20 21:51
 * author: fangjie24
 */
@Component
public class ModifiedRequestFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 修改请求内容
//        return null;
        return new ModifiedRequestDecorator(exchange, new RewriteConfig()
                .setRewriteFunction(String.class, String.class, (ex, requestData)
                        ->  Mono.just(modify(requestData))
                )).filter(exchange, chain);
    }

    private String modify(String requestData) {
        return "xxxx";
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
