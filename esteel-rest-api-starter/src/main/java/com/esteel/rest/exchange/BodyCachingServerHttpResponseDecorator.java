package com.esteel.rest.exchange;

import io.netty.buffer.UnpooledByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.slf4j.MDC;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static reactor.core.scheduler.Schedulers.single;

@Slf4j
public class BodyCachingServerHttpResponseDecorator extends ServerHttpResponseDecorator {

    private final StringBuilder body = new StringBuilder();

    private String requestId;
    private long entryTime;

    public BodyCachingServerHttpResponseDecorator(ServerHttpResponse delegate) {
        super(delegate);

        this.requestId = MDC.get("REQ_ID");
        this.entryTime = System.currentTimeMillis();
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        return super.writeWith(wrap(body));
    }

    @Override
    public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        return super.writeAndFlushWith(body);
    }

    private Publisher<? extends DataBuffer> wrap(Publisher<? extends DataBuffer> body) {
        if (body instanceof Mono) {
            Mono<?> mono = (Mono) body;
            return mono.publishOn(single()).map(buffer -> cache(this.requestId, (DataBuffer) buffer));
        } else if (body instanceof Flux) {
            Flux<?> flux = (Flux) body;
            return flux.publishOn(single()).map(buffer -> cache(this.requestId, (DataBuffer) buffer));
        } else {
            return body;
        }
    }

    private DataBuffer cache(String requestId, DataBuffer buffer) {
        String value = UTF_8.decode(buffer.asByteBuffer()).toString();
        this.body.append(value);
        DataBufferUtils.release(buffer);

        long interval = System.currentTimeMillis() - entryTime;
        HttpStatus statusCode = getStatusCode();
        String headers = getResponseHeaders(this);
        mdc(requestId);
        log.debug("=> HTTP>RES {}: {} {} {}", interval, statusCode, headers, body);

        NettyDataBufferFactory dataBufferFactory = new NettyDataBufferFactory(new UnpooledByteBufAllocator(false));
        return dataBufferFactory.wrap(value.getBytes(UTF_8));
    }

    private static void mdc(String requestId) {
        MDC.put("REQ_ID", requestId);
    }

    private static String getResponseHeaders(ServerHttpResponse response) {
        return response.getHeaders().entrySet().stream().
                map(v -> v.getKey() + ": " + String.join(",", v.getValue())).
                collect(Collectors.joining(BodyCachingServerWebExchangeDecorator.LINE_SEPARATOR));
    }
}
