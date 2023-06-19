package com.think.reactor.operators;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 17:53:00
 */
public class Collect {
    public static void main(String[] args) {
        Flux<Integer> just = Flux.range(1, 13);
        collectByCollectionAndBiConsumer(just);
        collectToList(just);
        collectToMap(just);
        collectToMultiMap(just);
    }

    private static void collectByCollectionAndBiConsumer(Flux<Integer> just) {
        Mono<LinkedList<Integer>> result = just
                .collect(() -> new LinkedList<>(), (list, integer) -> {
                    //保存元素到集合中之前,可以对元素进行条件判断、加工
                    if (integer < 10 && integer % 2 != 0) {
                        list.add(integer * 3);
                    }
                });
        StepVerifier.create(result)
                .expectNext(new LinkedList<>(Arrays.asList(3, 9, 15, 21, 27)))
                .verifyComplete();
    }

    private static void collectToList(Flux<Integer> just) {
        Mono<List<Integer>> result = just.collectList();
        StepVerifier.create(result)
                .expectNext(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13))
                .verifyComplete();
    }

    private static void collectToMap(Flux<Integer> just) {
        Mono<Map<String, Integer>> result = just.collectMap((i -> i + ""));
        Map<String, Integer> expectMap = new HashMap<>(12);
        just.subscribe(i -> expectMap.put((i + ""), i));
        StepVerifier.create(result)
                .expectNext(expectMap);
    }

    private static void collectToMultiMap(Flux<Integer> just) {
        Mono<Map<String, Collection<Integer>>> result = just.collectMultimap(i -> {
            //根据奇偶构造出两个key
            return i % 2 == 0 ? "odd" : "even";
        });
        Map<String, Collection<Integer>> expectMap = new HashMap<>(2);
        expectMap.put("odd", new ArrayList<>());
        expectMap.put("even", new ArrayList<>());
        just.subscribe(i -> {
            if (i % 2 == 0) {
                expectMap.get("odd").add(i);
            } else {
                expectMap.get("even").add(i);
            }
        });
        StepVerifier.create(result)
                .expectNext(expectMap)
                .verifyComplete();
    }
}
