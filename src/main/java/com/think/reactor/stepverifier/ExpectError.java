package com.think.reactor.stepverifier;

import com.think.reactor.DivideInterSupplier;
import reactor.core.publisher.Flux;

import reactor.test.StepVerifier;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 14:03:00
 */
public class ExpectError {

    public static void main(String[] args) {
        Flux<Integer> integerFluxWithException = Flux.just(
                        new DivideInterSupplier(1, 2),
                        new DivideInterSupplier(8, 2),
                        new DivideInterSupplier(20, 10),
                        //异常数据,抛出ArithmeticException
                        new DivideInterSupplier(1, 0),
                        new DivideInterSupplier(2, 2)
                )
                .map(divideIntegerSupplier -> divideIntegerSupplier.get());

        StepVerifier.create(integerFluxWithException)
                .expectNext(1 / 2)
                .expectNext(8 / 2)
                .expectNext(20 / 10)
                //校验异常数据，可以判断抛出的异常的类型是否符合预期
                .expectError(ArithmeticException.class)
                .verify();
    }
}
