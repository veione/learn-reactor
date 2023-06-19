package com.think.reactor.errorhandler;

import reactor.core.publisher.Flux;

/**
 * 异常默认返回值
 *
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 20:01:00
 */
public class StaticFallbackValue {

    public static void main(String[] args) {
        Flux<Integer> flux = Flux.just(0)
                .map(i -> 1 / i)
                //异常时返回0
                .onErrorReturn(0);
        //输出应该为0
        flux.log().subscribe(System.out::println);
    }
}
