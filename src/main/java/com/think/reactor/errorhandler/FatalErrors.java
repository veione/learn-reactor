package com.think.reactor.errorhandler;

import reactor.core.publisher.Flux;

/**
 * 有一些异常、错误，Reactor是不会传播，而是直接抛出的
 * 这些异常由Exceptions.throwIfFatal方法判断
 *
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 20:02:00
 */
public class FatalErrors {

    public static void main(String[] args) {
        Flux.just("")
                .map(s -> {
                    throw new UnknownError(s);
                })
                .doOnError(throwable -> System.err.println("doOnError " + throwable.getMessage()))
                .onErrorReturn("UnknownError")
                .log()
                //不会传播异常,即不会触发doOnError和onErrorReturn,而是直接抛出
                .subscribe(System.out::println, System.err::println);
    }
}
