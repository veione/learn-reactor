package com.think.reactor.factory;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * defer 构造出的Flux流，每次调用subscribe方法时，都会从Supplier获取Publisher实例作为输入。
 * 如果Supplier每次返回的实例不同，则可以构造出subscribe次数相关的Flux源数据流。
 * 如果每次都返回相同的实例，则和from(Publisher<? extends T> source)效果一样。
 *
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 10:47:00
 */
public class Defer {

    public static void main(String[] args) {
        AtomicInteger subscribeTime = new AtomicInteger(1);
        //实现这一的效果，返回的数据流为1~5乘以当前subscribe的次数
        Supplier<? extends Publisher<Integer>> supplier = () -> {
            Integer[] array = {1, 2,3,4,5};
            int currentTime = subscribeTime.getAndIncrement();
            for (var i = 0; i < array.length; i++) {
                array[i] *= currentTime;
            }
            return Flux.fromArray(array);
        };

        Flux<Integer> deferedFlux = Flux.defer(supplier);

        subscribe(deferedFlux, subscribeTime);
        subscribe(deferedFlux, subscribeTime);
        subscribe(deferedFlux, subscribeTime);
    }

    private static void subscribe(Flux<Integer> deferedFlux, AtomicInteger subscribeTime) {
        System.out.println("Subscribe time is " + subscribeTime.get());
        deferedFlux.subscribe(System.out::println);
    }
}
