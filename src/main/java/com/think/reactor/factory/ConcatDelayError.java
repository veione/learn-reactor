package com.think.reactor.factory;

import reactor.core.publisher.Flux;

/**
 * concatDelayError 和 concat的方法功能相同,唯一不同在于异常处理。
 * concatDelayError 会等待所有的流处理完成之后，再将异常传播下去。
 *
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 10:43:00
 */
public class ConcatDelayError {

    public static void main(String[] args) {
        Flux<Integer> sourceWithErrorNumberFormat = Flux.just("1", "2", "3", "4", "Five")
                .map(Integer::parseInt);
        Flux<Integer> source = Flux.just("5", "6", "7", "8", "9")
                .map(Integer::parseInt);

        Flux<Integer> concated = Flux.concatDelayError(sourceWithErrorNumberFormat, source);
        concated.subscribe(new MySubscriber("concatDelayError"));
    }
}





















