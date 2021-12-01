package org.example.filters;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jd.jsf.gd.GenericService;
import com.jd.jsf.gd.config.ConsumerConfig;
import com.jd.jsf.gd.config.RegistryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.setAlreadyRouted;

/**
 * description: filter
 * 最好以GatewayFilterFactory结尾，如XxxGatewayFilterFactory，在yml中的filter名称，为Xxx
 * date: 2021/10/11 11:20
 * author: fangjie24
 */
public class JsfGatewayFilterFactory extends AbstractGatewayFilterFactory<JsfGatewayFilterFactory.Config> {


    private static final Logger LOGGER = LoggerFactory.getLogger(LogGlobalFilter.class);

    /**
     * 前置协议
     */
    private final static String PRE_PROTOCOL = "jsf";

    public JsfGatewayFilterFactory() {
        super(Config.class);
    }



    /**
     * 简易配置声明参数对应顺序
     * @return
     */
    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("uri");
    }


    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {
            if (config == null || !config.getUri().startsWith(PRE_PROTOCOL)) {
                return chain.filter(exchange);
            }

            LOGGER.info("JsfGatewayFilterFactory");
            setAlreadyRouted(exchange);

            String[] split = config.getUri().replace("jsf://", "").split("/");
            String interfaceName = split[0];
            String methodName = split[1];
            String alias = split[2];
            String paramType = split[3];
            // 注册中心实现（必须）
            RegistryConfig jsfRegistry = new RegistryConfig();
            jsfRegistry.setIndex("i.jsf.jd.com"); // 测试环境192.168.150.121 i.jsf.jd.com
            LOGGER.info("实例RegistryConfig");
            // 服务消费者连接注册中心，设置属性
            ConsumerConfig<GenericService> consumerConfig = new ConsumerConfig<GenericService>();
            consumerConfig.setRegistry(jsfRegistry);
            consumerConfig.setProtocol("jsf");
            consumerConfig.setSerialization("hessian");
            consumerConfig.setInterfaceId(interfaceName);// 这里写真实的类名
            consumerConfig.setAlias(alias);
            consumerConfig.setGeneric(true); // 需要指定是Generic调用true
            LOGGER.info("实例ConsumerConfig");
            // 得到泛化调用实例，此操作很重，请缓存consumerConfig或者service对象！！！！（用map或者全局变量）
            GenericService service = consumerConfig.refer();
            JSONObject inputParam = new JSONObject();
            inputParam.put("userInfo",new JSONObject());
            inputParam.put("datas",new JSONObject());
            Object o = service.$invoke(methodName, new String[]{paramType}, new Object[]{inputParam});
            LOGGER.info("JsfGatewayFilterFactory，o={}", o);
            return chain.filter(exchange);
        };
    }

    /**
     * JSF过滤器配置项
     */
    public static class Config {
        /**
         * JSF地址,jsf://{接口全路径}/{方法名}
         */
        private String uri;

        public Config(String uri) {
            this.uri = uri;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }
    }


    public static void main(String[] args) {
        // 注册中心实现（必须）
        RegistryConfig jsfRegistry = new RegistryConfig();
        jsfRegistry.setIndex("i.jsf.jd.com"); // 测试环境192.168.150.121 i.jsf.jd.com
        LOGGER.info("实例RegistryConfig");
        // 服务消费者连接注册中心，设置属性
        ConsumerConfig<GenericService> consumerConfig = new ConsumerConfig<GenericService>();
        consumerConfig.setRegistry(jsfRegistry);
        consumerConfig.setProtocol("jsf");
        consumerConfig.setSerialization("hessian");
        consumerConfig.setInterfaceId("com.jd.jr.bill.service.abs.pzt.BillOpenPztService");// 这里写真实的类名
        consumerConfig.setAlias("abs-pzt-open-api-beta");
        consumerConfig.setGeneric(true); // 需要指定是Generic调用true
        consumerConfig.setTimeout(400000);
        LOGGER.info("实例ConsumerConfig");
        // 得到泛化调用实例，此操作很重，请缓存consumerConfig或者service对象！！！！（用map或者全局变量）
        GenericService service = consumerConfig.refer();
        String xx = "{\"systemId\":\"pzj_jc\",\"systemToken\":\"pzj1461279052836290560\",\"requestNo\":\"R123123123\",\"draftNo\":\"230839102613220211116144800857\",\"draftAmount\":\"106\",\"companyUserId\":\"C1002595400\"}";
        JSONObject inputParam = JSONObject.parseObject(xx);
        Object o = service.$invoke("validateDraftSign", new String[]{"com.jd.jr.bill.domain.request.abs.pzt.PztValidateSignReq"}, new Object[]{inputParam});
        System.out.println(o);
    }
}
