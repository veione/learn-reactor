package com.think.reactor.operators;

import reactor.core.publisher.Flux;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 19:25:00
 */
public class MapOperator {

    public static void main(String[] args) {
        //不根据源元素映射
        Flux.range(0, 10)
                //无论源元素的值是什么，都映射为硬编码的值
                .map(integer -> "test value")
                .subscribe(System.out::println);

        //根据源元素，映射为其sin值
        Flux.range(0, 10)
                .map(integer -> Math.sin(integer))
                .subscribe(System.out::println);

    }
}
