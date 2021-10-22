package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorResourceFactory;

/**
 * description: NettyConfig
 * date: 2021/10/20 9:55
 * author: fangjie24
 */
@Configuration
public class NettyConfig {

    @Bean
    public ReactorResourceFactory reactorResourceFactory() {
        System.setProperty("reactor.netty.ioWorkerCount", "10");
        return new ReactorResourceFactory();
    }
}
