package com.think.reactor.debugging;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 11:36:00
 */
public class DebugMode {

    public static void main(String[] args) {
        Flux.just("0", "1", "2", "abc")
                .map(i -> Integer.parseInt(i) + "")
                .subscribe(System.out::println);

        //开启操作符调试
        Hooks.onOperatorDebug();

        Flux.just("0", "1", "2", "abc")
                .map(i -> Integer.parseInt(i) + "")
                .subscribe(System.out::println);
    }
}
