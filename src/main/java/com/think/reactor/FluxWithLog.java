package com.think.reactor;

import reactor.core.publisher.Flux;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 10:24:00
 */
public class FluxWithLog {

    public static void main(String[] args) {
        //subscribe with consumer and error handler
        Flux<DivideInterSupplier> integerFluxWithException = Flux.just(
                new DivideInterSupplier(1, 2),
                new DivideInterSupplier(8, 2),
                new DivideInterSupplier(20, 10),
                new DivideInterSupplier(1, 0),//异常数据，抛出ArithmeticException
                new DivideInterSupplier(2, 2)
        ).log();
        integerFluxWithException.subscribe(
                integer -> integer.get(),
                throwable -> System.out.println("get error " + throwable.getMessage())
        );
    }
}
