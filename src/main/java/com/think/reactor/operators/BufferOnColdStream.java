package com.think.reactor.operators;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 16:47:00
 */
public class BufferOnColdStream {
    private static Flux<String> just = Flux.just("a", "b", "c", "d", "e");

    public static void main(String[] args) {
        //使用Flux默认的ArrayList作为buffer
        buffer();
        bufferWithMax();
        bufferWithMaxBiggerThanSkip();
        bufferWithSkipBiggerThanMax();
        bufferWithMaxEqualsSkip();

        //使用用户自定义的Collection作为buffer
        bufferViaCollection();

        //根据元素满足的条件进行buffer
        bufferWhile();
        System.out.println("All test success.");
    }

    private static void buffer() {
        StepVerifier.create(
                        just.buffer()
                ).expectNext(new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e")))
                .verifyComplete();
    }

    private static void bufferWithMax() {
        StepVerifier.create(
                        //buffer 大小为2,第一次返回ab,第二次返回cd,第三次返回e
                        just.buffer(2)
                ).expectNext(new ArrayList<>(Arrays.asList("a", "b")))
                .expectNext(new ArrayList<>(Arrays.asList("c", "d")))
                .expectNext(new ArrayList<>(Arrays.asList("e")))
                .verifyComplete();
    }

    private static void bufferWithMaxBiggerThanSkip() {
        StepVerifier.create(
                        //buffer 为2,skip为1,元素共5个，返回值下标为：
                        //(0,1) (1,2),(2,3),(4)
                        just.buffer(2, 1)
                ).expectNext(new ArrayList<>(Arrays.asList("a", "b")))
                .expectNext(new ArrayList<>(Arrays.asList("b", "c")))
                .expectNext(new ArrayList<>(Arrays.asList("c", "d")))
                .expectNext(new ArrayList<>(Arrays.asList("d", "e")))
                .expectNext(new ArrayList<>(Arrays.asList("e")))
                .verifyComplete();
    }

    private static void bufferWithSkipBiggerThanMax() {
        StepVerifier.create(
                        //buffer 为2,skip为3,元素共5个，返回值下标为：
                        //(0,1)(3,4)
                        just.buffer(2, 3)
                ).expectNext(new ArrayList<>(Arrays.asList("a", "b")))
                .expectNext(new ArrayList<>(Arrays.asList("d", "e")))
                .verifyComplete();
    }

    private static void bufferWithMaxEqualsSkip() {
        StepVerifier.create(
                        //buffer为2,skip为2,元素共5个，返回值下标为：
                        //(0,1) (2,3) (4)
                        //即当maxSize等于skip时,等价于只有maxSize
                        just.buffer(2, 2)
                ).expectNext(new ArrayList<>(Arrays.asList("a", "b")))
                .expectNext(new ArrayList<>(Arrays.asList("c", "d")))
                .expectNext(new ArrayList<>(Arrays.asList("e")))
                .verifyComplete();
    }

    private static void bufferViaCollection() {
        StepVerifier.create(
                        just.buffer(3, () -> new LinkedList<>())
                ).expectNext(new LinkedList<>(Arrays.asList("a", "b", "c")))
                .expectNext(new LinkedList<>(Arrays.asList("d", "e")))
                .verifyComplete();
    }

    private static void bufferWhile() {
        StepVerifier.create(
                        Flux.just(1, 3, 5, 2, 4, 6, 11, 12, 13)
                                .bufferWhile(i -> i % 2 == 0)
                )
                //注意：bufferWhile会将满足条件的元素放入List中，直到下一个元素
                //不满足条件，所以这里会返回两次，因为6和12之间有一个不满足条件的11
                .expectNext(new ArrayList<>(Arrays.asList(2, 4, 6)))
                .expectNext(new ArrayList<>(Arrays.asList(12)))
                .verifyComplete();
    }
}
