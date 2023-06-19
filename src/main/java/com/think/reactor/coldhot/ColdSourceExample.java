package com.think.reactor.coldhot;

import reactor.core.publisher.Flux;

/**
 * 在 Reactor 中，数据源可以分为冷数据源和热数据源，它们的主要区别在于数据的产生时间和传递方式。
 * <p>
 * 冷数据源是指在订阅者订阅该数据源之后，数据源再开始产生数据，并且每个订阅者都会独立地接收到完整的数据流。
 * 换句话说，冷数据源是按需请求数据的，并且每个订阅者都会接收到相同的数据流。
 * 冷数据源最常见的例子是从文件、数据库、网络等 IO 渠道获取数据的情况。
 * <p>
 * 可以使用 Flux.create() 方法创建一个冷数据源，该方法需要传入一个 FluxSink 对象，用于向订阅者发送数据。
 *
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 13:51:00
 */
public class ColdSourceExample {

    public static void main(String[] args) {
        Flux<String> source = Flux.create(sink -> {
            System.out.println("Starting to emit data");
            sink.next("A");
            sink.next("B");
            sink.next("C");
            sink.complete();
        });

        source.subscribe(data -> System.out.println("Subscriber 1 received: " + data));
        source.subscribe(data -> System.out.println("Subscriber 2 received: " + data));
    }
}
