package com.think.reactor;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.SignalType;

/**
 * 自定义的Subscriber
 *
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 10:02:00
 */
public class MySubscriber<T> extends BaseSubscriber<T> {

    @Override
    protected void hookOnSubscribe(Subscription subscription) {
        System.out.println("MySubscriber Subscribed");
        request(1);
    }

    @Override
    protected void hookOnNext(T value) {
        System.out.println("get value：" + value);
        request(1);
    }

    @Override
    protected void hookOnComplete() {
        System.out.println("No error and complete");
    }

    @Override
    protected void hookOnError(Throwable throwable) {
        System.out.println("Get error: " + throwable.getCause());
        super.hookOnError(throwable);
    }

    @Override
    protected void hookOnCancel() {
        System.out.println("Subscribe canceled");
    }

    @Override
    protected void hookFinally(SignalType type) {
        System.out.println("Finally finished");
    }
}
