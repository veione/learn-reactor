package com.think.reactor.factory;

import reactor.core.publisher.Flux;

/**
 * concat 及其重载方法接收多个 Publisher 拼接作为一个新的Flux返回,
 * 返回元素时首先返回接收到的第一个 Publisher 流中的元素，直到第一个 Publisher
 * 流结束之后，才开始返回第二个 Publisher 流中的元素，以此类推...
 * 如果发生异常，Flux流会立刻异常终止
 *
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 10:33:00
 */
public class Concat {

    public static void main(String[] args) {
        Flux<Integer> source1 = Flux.range(1, 5);
        Flux<Integer> source2 = Flux.range(6, 5);

        Flux<Integer> concated  = Flux.concat(source1, source2);
        concated.subscribe(new MySubscriber("concated"));
    }
}
