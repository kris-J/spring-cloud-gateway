package org.example.config;

import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.filter.factory.rewrite.GzipMessageBodyResolver;
import org.springframework.cloud.gateway.filter.factory.rewrite.MessageBodyDecoder;
import org.springframework.cloud.gateway.filter.factory.rewrite.MessageBodyEncoder;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import static java.util.function.Function.identity;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR;
public class ModifiedResponseDecorator extends ServerHttpResponseDecorator {

    private final ServerWebExchange exchange;

    private final RewriteConfig config;

    private final Map<String, MessageBodyDecoder> messageBodyDecoders ;
    private final Map<String, MessageBodyEncoder> messageBodyEncoders;

    private final List<HttpMessageReader<?>> messageReaders = HandlerStrategies.withDefaults().messageReaders();


    public ModifiedResponseDecorator(ServerWebExchange exchange, RewriteConfig config) {
        super(exchange.getResponse());
        this.exchange = exchange;
        this.config = config;
        Set<MessageBodyDecoder> messageBodyDecodersSet = new HashSet<>();
        Set<MessageBodyEncoder> messageBodyEncodersSet = new HashSet<>();
        MessageBodyDecoder messageBodyDecoder = new GzipMessageBodyResolver();
        MessageBodyEncoder messageBodyEncoder = new GzipMessageBodyResolver();
        messageBodyDecodersSet.add(messageBodyDecoder);
        messageBodyEncodersSet.add(messageBodyEncoder);
        this.messageBodyDecoders = messageBodyDecodersSet.stream()
                .collect(Collectors.toMap(MessageBodyDecoder::encodingType, identity()));
        this.messageBodyEncoders = messageBodyEncodersSet.stream()
                .collect(Collectors.toMap(MessageBodyEncoder::encodingType, identity()));
    }


    @SuppressWarnings("unchecked")
    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {

        Class inClass = config.getInClass();
        Class outClass = config.getOutClass();

        String originalResponseContentType = exchange.getAttribute(ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);
        HttpHeaders httpHeaders = new HttpHeaders();
        // explicitly add it in this way instead of
        // 'httpHeaders.setContentType(originalResponseContentType)'
        // this will prevent exception in case of using non-standard media
        // types like "Content-Type: image"
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, originalResponseContentType);

        ClientResponse clientResponse = prepareClientResponse(body, httpHeaders);

        // TODO: flux or mono
        Mono modifiedBody = extractBody(exchange, clientResponse, inClass)
                .flatMap(originalBody -> config.getRewriteFunction().apply(exchange, originalBody))
                .switchIfEmpty(Mono.defer(() -> (Mono) config.getRewriteFunction().apply(exchange, null)));

        BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, outClass);
        CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange,
                exchange.getResponse().getHeaders());
        return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {
            Mono<DataBuffer> messageBody = writeBody(getDelegate(), outputMessage, outClass);
            HttpHeaders headers = getDelegate().getHeaders();
            if (!headers.containsKey(HttpHeaders.TRANSFER_ENCODING)
                    || headers.containsKey(HttpHeaders.CONTENT_LENGTH)) {
                messageBody = messageBody.doOnNext(data -> headers.setContentLength(data.readableByteCount()));
            }
            // TODO: fail if isStreamingMediaType?
            return getDelegate().writeWith(messageBody);
        }));
    }

    @Override
    public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        return writeWith(Flux.from(body).flatMapSequential(p -> p));
    }

    private ClientResponse prepareClientResponse(Publisher<? extends DataBuffer> body, HttpHeaders httpHeaders) {
        ClientResponse.Builder builder;
        builder = ClientResponse.create(exchange.getResponse().getStatusCode(), messageReaders);
        return builder.headers(headers -> headers.putAll(httpHeaders)).body(Flux.from(body)).build();
    }

    private <T> Mono<T> extractBody(ServerWebExchange exchange, ClientResponse clientResponse, Class<T> inClass) {
        // if inClass is byte[] then just return body, otherwise check if
        // decoding required
        if (byte[].class.isAssignableFrom(inClass)) {
            return clientResponse.bodyToMono(inClass);
        }

        List<String> encodingHeaders = exchange.getResponse().getHeaders().getOrEmpty(HttpHeaders.CONTENT_ENCODING);
        for (String encoding : encodingHeaders) {
            MessageBodyDecoder decoder = messageBodyDecoders.get(encoding);
            if (decoder != null) {
                return clientResponse.bodyToMono(byte[].class).publishOn(Schedulers.parallel()).map(decoder::decode)
                        .map(bytes -> exchange.getResponse().bufferFactory().wrap(bytes))
                        .map(buffer -> prepareClientResponse(Mono.just(buffer),
                                exchange.getResponse().getHeaders()))
                        .flatMap(response -> response.bodyToMono(inClass));
            }
        }

        return clientResponse.bodyToMono(inClass);
    }

    private Mono<DataBuffer> writeBody(ServerHttpResponse httpResponse, CachedBodyOutputMessage message,
                                       Class<?> outClass) {
        Mono<DataBuffer> response = DataBufferUtils.join(message.getBody());
        if (byte[].class.isAssignableFrom(outClass)) {
            return response;
        }

        List<String> encodingHeaders = httpResponse.getHeaders().getOrEmpty(HttpHeaders.CONTENT_ENCODING);
        for (String encoding : encodingHeaders) {
            MessageBodyEncoder encoder = messageBodyEncoders.get(encoding);
            if (encoder != null) {
                DataBufferFactory dataBufferFactory = httpResponse.bufferFactory();
                response = response.publishOn(Schedulers.parallel()).map(buffer -> {
                    byte[] encodedResponse = encoder.encode(buffer);
                    DataBufferUtils.release(buffer);
                    return encodedResponse;
                }).map(dataBufferFactory::wrap);
                break;
            }
        }
        return response;
    }

}