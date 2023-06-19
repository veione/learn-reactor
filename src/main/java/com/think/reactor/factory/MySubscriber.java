package com.think.reactor.factory;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 10:36:00
 */
public class MySubscriber implements Subscriber<Integer> {
    private String name;

    public MySubscriber(String name) {
        this.name = name;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        System.out.println(this.name + " onSubscribe");
        subscription.request(Integer.MAX_VALUE);
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println(this.name + " onError: " + throwable.getMessage());
    }

    @Override
    public void onComplete() {
        System.out.println(this.name + " onComplete");
    }

    @Override
    public void onNext(Integer item) {
        System.out.println(this.name + " get item " + item);
    }
}
