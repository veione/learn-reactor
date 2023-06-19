package com.think.reactor.factory;

import reactor.core.publisher.Flux;

import java.nio.charset.Charset;
import java.util.SortedMap;
import java.util.stream.Stream;

/**
 * fromStream方法通过给定的流数据接口构造出一个Flux数据流
 *
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 10:59:00
 */
public class FromStream {

    public static void main(String[] args) {
        SortedMap<String, Charset> charSetMap = Charset.availableCharsets();
        Stream<String> charSetStream = charSetMap.keySet().stream();
        Flux<String> charsetFlux = Flux.fromStream(charSetStream);
        charsetFlux.subscribe(System.out::println);
    }
}
