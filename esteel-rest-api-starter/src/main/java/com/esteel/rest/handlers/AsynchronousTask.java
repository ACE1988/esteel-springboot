package com.esteel.rest.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Slf4j
@Component
public class AsynchronousTask {

    @Async("asyncExecutor")
    public CompletableFuture<String> executeTask(String id, Consumer<String> consumer) {
        try {
            log.debug("asynchronous task {} start.", id);
            consumer.accept(id);
            log.debug("asynchronous task {} complete.", id);
        } catch (Exception e) {
            log.error("asynchronous task {} failed.", id, e);
        }
        return CompletableFuture.completedFuture(id);
    }
}
