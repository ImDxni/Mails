package org.waraccademy.posta.database.mongo.subscribers;

import org.waraccademy.mongo.database.subscribers.OperationSubscriber;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class CompletableSubscriber<T,V> extends OperationSubscriber<T> {
    private final CompletableFuture<V> future = new CompletableFuture<>();
    private final Function<T,V> function;

    public CompletableSubscriber(Function<T,V> function) {
        this.function = function;
    }

    @Override
    public void onComplete() {
        super.onComplete();
        if(!getReceived().isEmpty()) {
            future.complete(function.apply(getReceived().get(0)));
        }
    }

    public CompletableFuture<V> getResult() {
        return future;
    }
}
