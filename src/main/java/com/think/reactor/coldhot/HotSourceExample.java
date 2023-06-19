package com.think.reactor.coldhot;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

/**
 * 热数据源是一种数据推送模型，它不是在订阅者订阅数据源之后开始产生数据，而是在数据源产生数据之后，将数据推送给所有的订阅者。
 * 因此，得益于热数据源的特性，所有的订阅者都会接收到相同的数据流，而且可能会错过一些数据。
 *
 * 可以使用 ConnectableFlux 对象创建一个热数据源，该对象可以在数据准备好之后通过 connect() 方法触发数据流的推送操作。
 *
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 13:53:00
 */
public class HotSourceExample {

    public static void main(String[] args) {
        //使用 publish() 方法将其转换成了一个 ConnectableFlux 对象，并未触发推送数据流的操作。
        ConnectableFlux<String> source = Flux.just("A", "B", "C").publish();

        //模拟两个订阅者订阅数据流
        source.subscribe(data -> System.out.println("Subscriber 1 received: " + data));
        source.subscribe(data -> System.out.println("Subscriber 2 received: " + data));

        //需要通过 connect() 方法来触发数据流的推送操作，这样所有的订阅者都会接收到相同的数据流
        source.connect();
    }
}
