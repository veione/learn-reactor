package com.think.reactor.stepverifier;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 14:11:00
 */
public class ThenConsumeWhile {

    public static void main(String[] args) {
        //素数数据流
        Flux<Integer> primes = Flux.range(1, 100)
                .filter(integer -> isPrime(integer));

        StepVerifier.create(primes)
                //素数肯定无法整除8
                .thenConsumeWhile(integer -> (integer % 8 != 0))
                .verifyComplete();
    }

    private static boolean isPrime(int n) {
        if (n < 2) {
            return false;
        }
        if (n == 2) {
            return true;
        }
        if (n % 2 == 0) {
            return false;
        }
        for (var i = 3; i < n; i += 2) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}
