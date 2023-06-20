package com.think.reactor.errorhandler;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;

/**
 * 异常时retry，每次retry的流都是新的流
 *
 * @author veione
 * @version 1.0.0
 * @date 2023年06月20日 09:44:00
 */
public class Retrying {

    public static void main(String[] args) throws InterruptedException {
        //默认异常retry
        Flux<String> flux = Flux.just("0", "1", "2", "abc")
                .map(n -> Integer.parseInt(n) + "")
                .retry(2);
        flux.subscribe(newSub());

        //带条件判断的retry
        System.out.println("====================================================================================");
        Thread.sleep(500);
        flux = Flux.just("0", "1", "2", "abc")
                .map(n -> Integer.parseInt(n) + "")
                //Retry.fixedDelay() 创建了一个 Retry 实例，它规定了重试的次数和每次重试的等待时间，同时也指定了重试的条件，即只在捕获 ArithmeticException 异常时才重试。
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)) //重试3次，每次等待2秒
                        .filter(throwable -> throwable instanceof NumberFormatException)
                );

        flux.subscribe(newSub());
        Thread.sleep(10000);
    }

    private static Subscriber<String> newSub() {
        return new BaseSubscriber<>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                System.out.println("start");
                request(1);
            }

            @Override
            protected void hookOnNext(String value) {
                System.out.println("get value is " + Integer.parseInt(value));
                request(1);
            }

            @Override
            protected void hookOnComplete() {
                System.out.println("Complete");
            }

            @Override
            protected void hookOnError(Throwable throwable) {
                System.err.println(throwable.getMessage());
            }
        };
    }
}
