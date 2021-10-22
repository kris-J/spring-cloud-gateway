package org.example.filters;

import org.example.config.ModifiedRequestDecorator;
import org.example.config.ModifiedResponseDecorator;
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
public class ModifiedResponseFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange.mutate().response(
                new ModifiedResponseDecorator(exchange, new RewriteConfig().
                        setRewriteFunction(String.class, String.class, (ex, responseData)
                                ->  Mono.just(modify(responseData))
                        ))).build());
    }

    private String modify(String responseData) {
        return "modify response:" + responseData;
    }

    @Override
    public int getOrder() {
        return -2;
    }
}
