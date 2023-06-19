package com.think.reactor.operators;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 18:27:00
 */
public class DelaySubscription {

    public static void main(String[] args) {
        Flux<Integer> just = Flux.just(1, 2, 3, 4, 5)
                .delaySubscription(Duration.ofSeconds(2));
        final long startTime = System.currentTimeMillis();
        just.subscribe(new BaseSubscriber<>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                super.hookOnSubscribe(subscription);
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                System.out.println("delay of Subscription is " + duration + " millis");
            }
        });
        just.blockLast();
    }
}
