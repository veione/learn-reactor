package com.think.reactor.operators;

import reactor.core.publisher.Flux;

/**
 * doOnXXX回调
 *
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 18:55:00
 */
public class DoOnX {

    public static void main(String[] args) {
        Flux<Integer> just = Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13);
        doOnEach(just);
        doOnNext(just);
        doOnRequest(just);
    }

    private static void doOnEach(Flux<Integer> just) {
        just.doOnEach(signal -> {
            System.out.println("doOnEach " + signal.get());
        }).subscribe(System.out::println);
    }

    private static void doOnNext(Flux<Integer> just) {
        //doOnNext中对元素的修改是不会影响到Flux中元素的值的
        just.doOnNext(integer -> {
            System.out.println("doOnNext " + (++integer));
        }).subscribe(System.out::println);
    }

    private static void doOnRequest(Flux<Integer> just) {
        //doOnRequest可以获取到Subscriber的request(N)中的N
        just.doOnRequest(n -> System.out.println("doOnRequest " + n))
                .subscribe(System.out::println);
    }
}
