package com.think.reactor;

import reactor.core.publisher.Flux;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 10:27:00
 */
public class FluxWithMySubscriber {

    public static void main(String[] args) {
        //subscribe with consumer and error handler and completeConsumer
        System.out.println("example for subscribe with consumer, error handler, completeConsumer and customerSubscriber");
        Flux<DivideInterSupplier> integerFlux = Flux.just(
                new DivideInterSupplier(1, 2),
                new DivideInterSupplier(8, 2),
                new DivideInterSupplier(20, 10),
                new DivideInterSupplier(2, 2)
        );
        MySubscriber<DivideInterSupplier> integerMySubscriber = new MySubscriber<>();
        integerFlux.subscribe(integer -> integer.get(),
                throwable -> throwable.getMessage(),
                () -> System.out.println("-- No Error and Finished --"),
                integer -> integerMySubscriber.request(5));
        integerFlux.subscribe(integerMySubscriber);
        System.out.println("this is end fo main");
    }
}
