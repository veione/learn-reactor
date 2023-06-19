package com.think.reactor;

import reactor.core.publisher.Flux;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 10:16:00
 */
public class FluxSubscriber {

    public static void main(String[] args) {
        Flux<String> stringFlow = Flux.just("one", "two", "three");

        //subscribe
        System.out.println("example for subscribe");
        stringFlow.subscribe();

        //subscribe with consumer
        System.out.println("example fro subscribe with consume");
        stringFlow.subscribe(System.out::println);

        //subscribe with consume and error handler
        System.out.println("example for subscribe with consumer and error handler");
        Flux<DivideInterSupplier> integerFluxWithException = Flux.just(
                new DivideInterSupplier(1, 2),
                new DivideInterSupplier(8, 2),
                new DivideInterSupplier(20, 10),
                new DivideInterSupplier(1, 0), //异常数据
                new DivideInterSupplier(2, 2)
        );

        integerFluxWithException.subscribe(
                integer -> System.out.println("get integer: " + integer.get()),
                throwable -> System.out.println("get error" + throwable.getMessage())
        );

        //subscriber with consumer and error handler and completeConsumer
        System.out.println("example for subscribe with consumer, error handler and completeConsumer ");
        Flux<DivideInterSupplier> integerFlux = Flux.just(
                new DivideInterSupplier(1, 2),
                new DivideInterSupplier(8, 2),
                new DivideInterSupplier(20, 10),
                new DivideInterSupplier(2, 2)
        );
        integerFlux.subscribe(
                integer -> System.out.println("get integer:" + integer.get()),
                throwable -> System.out.println("get error"+ throwable.getMessage()),
                () -> System.out.println("No Error and Finished")
        );
    }
}
