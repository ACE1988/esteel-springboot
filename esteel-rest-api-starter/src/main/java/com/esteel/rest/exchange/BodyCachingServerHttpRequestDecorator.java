package com.esteel.rest.exchange;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public class BodyCachingServerHttpRequestDecorator extends ServerHttpRequestDecorator {

    private final StringBuilder body = new StringBuilder();

    private List<String> authorization;

    public BodyCachingServerHttpRequestDecorator(ServerHttpRequest delegate) {
        super(delegate);

        URI uri = delegate.getURI();
        String queryString = uri.getQuery();

        this.authorization = delegate.getHeaders().get("Authorization");
        if (StringUtils.isNotBlank(queryString)) {
            log.debug("=> HTTP>REQ: |{}| {}", queryString, this.authorization);
        }
    }

    @Override
    public MultiValueMap<String, String> getQueryParams() {
        return super.getQueryParams();
    }

    @Override
    public Flux<DataBuffer> getBody() {
        return super.getBody().doOnNext(this::cache);
    }

    @SneakyThrows
    private void cache(DataBuffer buffer) {
        this.body.append(UTF_8.decode(buffer.asByteBuffer()).toString());
        if (StringUtils.isNotBlank(this.body)) {
            log.debug("=> HTTP>REQ: |{}| {}", this.body, this.authorization);
        }
    }
}
