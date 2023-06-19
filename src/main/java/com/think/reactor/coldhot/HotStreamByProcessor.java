package com.think.reactor.coldhot;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.UnicastProcessor;

import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 11:45:00
 */
public class HotStreamByProcessor {

    public static void testNewVersion() {
        //创建一个Sinks.Many序列
        //Sinks.many().unicast() 是 Reactor 的一个发送多个元素的消息源，只有订阅者订阅后才会开始发出消息，且每个订阅者都会独立地接收到所有的消息。
        // 创建一个 Sinks.Many 序列
        Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

        // 将 Sinks.Many 序列转换为 Flux 对象
        Flux<String> flux = sink.asFlux().publish().autoConnect(2);

        // 创建订阅者 1，并订阅消息
        flux.subscribe(data -> System.out.println("Subscriber 1 received: " + data));

        // 创建订阅者 2，并订阅消息
        flux.subscribe(data -> System.out.println("Subscriber 2 received: " + data));

        // 向 Sinks.Many 序列发送 3 个元素
        sink.tryEmitNext("A");
        sink.tryEmitNext("B");
        sink.tryEmitNext("C");

        // 向 Sinks.Many 序列再发送 2 个元素
        sink.tryEmitNext("D");
        sink.tryEmitNext("E");

        // 结束 Sinks.Many 序列，并发送 complete 信号
        sink.tryEmitComplete();
    }

    public static void main(String[] args) throws InterruptedException {
        //testNewVersion();

        //使用Reactor提供的Processor工具类
        Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

        //构造Hot Stream，同时配置为autoConnect，避免每加入一个Subscriber都需要调用一次connect方法
        Flux<String> hotFlux = sink.asFlux().publish().autoConnect(2);

        //异步为Hot Stream提供数据
        CompletableFuture future = CompletableFuture.runAsync(() -> {
            IntStream.range(0, 10).forEach(
                    value -> {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //调用Sinks的tryEmitNext即可以为Sinks关联的Hot Stream提供数据
                        sink.tryEmitNext("value is " + value);
                    }
            );
        });

        hotFlux.subscribe(s -> System.out.println("subscriber 1: " + s));

        Thread.sleep(500);
        hotFlux.subscribe(s -> System.out.println("subscriber 2: " + s));
        //提供完数据之后，调用Sinks的tryEmitComplete关闭Hot Stream
        future.thenRun(() -> sink.tryEmitComplete());
        future.join();
    }
}
