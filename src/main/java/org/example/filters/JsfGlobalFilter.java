package org.example.filters;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jd.jsf.gd.GenericService;
import com.jd.jsf.gd.config.ConsumerConfig;
import com.jd.jsf.gd.config.RegistryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * description: JsfGlobalFilter
 * date: 2021/10/11 11:48
 * author: fangjie24
 */
@Component
public class JsfGlobalFilter implements GlobalFilter, Ordered {


    private static final Logger LOGGER = LoggerFactory.getLogger(JsfGlobalFilter.class);

    private volatile Map<String, GenericService> urlRelJsfMap = new HashMap<>();

    @PostConstruct
    private void init() {
        // 注册中心实现（必须）
        RegistryConfig jsfRegistry = new RegistryConfig();
        jsfRegistry.setIndex("i.jsf.jd.com"); // 测试环境192.168.150.121 i.jsf.jd.com
        LOGGER.info("实例RegistryConfig");
        // 服务消费者连接注册中心，设置属性
        ConsumerConfig<GenericService> consumerConfig = new ConsumerConfig<GenericService>();
        consumerConfig.setRegistry(jsfRegistry);
        consumerConfig.setProtocol("jsf");
        consumerConfig.setSerialization("hessian");
        consumerConfig.setInterfaceId("com.jd.jr.cbp.abs.api.facade.commercial.DraftFacade");// 这里写真实的类名
        consumerConfig.setAlias("local");
        consumerConfig.setGeneric(true); // 需要指定是Generic调用true
        LOGGER.info("实例ConsumerConfig");
        // 得到泛化调用实例，此操作很重，请缓存consumerConfig或者service对象！！！！（用map或者全局变量）
        GenericService service = consumerConfig.refer();
        urlRelJsfMap.put("com.jd.jr.cbp.abs.api.facade.commercial.DraftFacade", service);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        if (route == null) {
            return chain.filter(exchange);
        }
        URI uri = route.getUri();
        LOGGER.info("uri={}",uri.toString());
        String scheme = uri.getScheme();
        if (!"jsf".equals(scheme)) {
            return chain.filter(exchange);
        }
        LOGGER.info("JsfGatewayFilterFactory");
        String interfaceName = uri.getHost();
        String[] split = uri.getPath().replaceFirst("/","").split("/");
        String methodName = split[0];
        String alias = split[1];
        String paramType = split[2];

        GenericService genericService = urlRelJsfMap.get(interfaceName);

        JSONObject inputParam = new JSONObject();
        inputParam.put("userInfo",new JSONObject());
        inputParam.put("datas",new JSONObject());
        Object o = "jsf result";//genericService.$invoke(methodName, new String[]{paramType}, new Object[]{inputParam});
        LOGGER.info("JsfGatewayFilterFactory，o={}", o);
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBuffer write = response.bufferFactory().allocateBuffer().write(JSON.toJSONString(o).getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(write));
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
