package com.think.reactor.errorhandler;

import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;

import java.util.function.Consumer;

/**
 * onFinally:类似于Finally语句，无论如何ON_COMPLETE都会触发
 *
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 19:49:00
 */
public class DoFinally {

    public static void main(String[] args) {
        Consumer<SignalType> finallyConsumer = signalType -> {
            switch (signalType) {
                case ON_COMPLETE -> System.out.println("Successfully complete");
                case CANCEL -> System.out.println("Canceled");
                case ON_ERROR -> System.out.println("System Error");
            }
        };

        //判断发生了异常
        Flux<String> flux = Flux.just("0", "1", "2", "abc")
                .map(i -> Integer.parseInt(i) + "")
                .doOnError(e -> e.printStackTrace())
                .onErrorReturn("System exception")
                .doFinally(finallyConsumer);
        //最后打印Successfully complete
        flux.log().subscribe(System.out::println);

        //判断cancel
        flux = Flux.just("0", "1", "2")
                .map(i -> Integer.parseInt(i) + "")
                .doOnError(e -> e.printStackTrace())
                .onErrorReturn("System exception")
                .doFinally(finallyConsumer);
        //最后打印Canceled
        flux.log()
                //take(1)会在处理1个元素之后cancel
                .take(1)
                .subscribe(System.out::println);
    }
}
