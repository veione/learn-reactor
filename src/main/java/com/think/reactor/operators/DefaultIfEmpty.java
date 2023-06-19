package com.think.reactor.operators;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 18:07:00
 */
public class DefaultIfEmpty {

    public static void main(String[] args) {
        Flux<List<Integer>> flux =
                //构建一个奇数序列
                Flux.just(1, 3, 5, 7, 9)
                        //只buffer偶数，所以不会返回任何数
                        .bufferWhile(integer -> integer % 2 == 0)
                        //默认返回一个空List
                        .defaultIfEmpty(Collections.emptyList());
        StepVerifier.create(flux)
                .expectNext(Collections.emptyList())
                .verifyComplete();
    }
}
