package com.think.reactor.create;

/**
 * 在 Reactor 中，prefetch 是指一次数据请求中可获取的元素数量。可以理解为在访问数据流中的元素时，一次能获取多少个元素。
 * 它会影响订阅者与发布者之间的关系，不同的 prefetch 值可以对数据流的性能产生不同的影响。
 * <p>
 * prefetch 可以在 Flux 和 Mono 中设置，可以使用 Flux::create()、Flux::generate() 或者 Flux::interval()
 * 等方法中的 bufferSize 或 prefetch 方法设置订阅者的请求数量。
 *
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 14:23:00
 */

import reactor.core.publisher.*;

public class PrefetchExample {
    public static void main(String[] args) {
        /**
         * 我们使用 Flux.create() 方法创建了一个数据源 source，并在 sink 中使用 next() 方法向订阅者发送了 3 个元素，最后使用 complete() 方法发送完成信号。
         * 然后我们通过 source.log() 方法添加了一个日志记录器，并使用 bufferSize() 方法设置缓冲区大小为 2，并使用 prefetch() 方法设置每次请求数量为 3。
         * 最后使用 subscribe() 方法将数据源订阅到订阅者中。
         *
         * 在这个示例中，当订阅者订阅数据源时，订阅者会向数据源发出请求，请求每次获取 3 个元素。由于数据源发送 3 个元素，所以只需要一次请求即可获取所有元素。
         *
         * 通过设置合理的 prefetch 值，可以更好地平衡订阅者和发布者之间的关系，提高数据流的性能。但是需要注意，prefetch 值也不是越大越好，过大的值可能会导致数据流的延迟和性能问题。
         */
        Flux<String> source = Flux.create(sink -> {
            System.out.println("Data source is ready");
            sink.next("A");
            sink.next("B");
            sink.next("C");
            sink.complete();
        });

        source
                .log()
                .buffer(2)
                .limitRate(3)
                .subscribe(data -> System.out.println("Subscriber received: " + data));
    }
}

