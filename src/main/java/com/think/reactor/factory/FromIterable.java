package com.think.reactor.factory;

import reactor.core.publisher.Flux;

import java.nio.charset.Charset;
import java.util.SortedMap;

/**
 * fromIterable方法通过给定的可迭代的接口对象构造出一个Flux流
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 10:53:00
 */
public class FromIterable {

    public static void main(String[] args) {
        SortedMap<String, Charset> charSetMap = Charset.availableCharsets();
        Iterable<String> iterable = charSetMap.keySet();

        Flux<String> charsetFlux = Flux.fromIterable(iterable);
        charsetFlux.subscribe(System.out::println);
    }
}
