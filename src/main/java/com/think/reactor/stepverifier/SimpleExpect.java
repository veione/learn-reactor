package com.think.reactor.stepverifier;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 14:08:00
 */
public class SimpleExpect {

    public static void main(String[] args) {
        StepVerifier.create(Flux.just("one", "two", "three"))
                //依次校验每一步的数据是否符合预期
                .expectNext("one")
                .expectNext("two")
                .expectNext("three")
                //校验Flux流是否按照预期正常关闭
                .expectComplete()
                //启动
                .verify();

        StepVerifier.create(Flux.just("one", "two", "three"))
                //依次校验每一步的数据是否符合预期
                .expectNext("one")
                .expectNext("two")
                //不满足预期,抛出异常
                .expectNext("Five")
                //校验Flux流是否按照预期正常关闭
                .expectComplete()
                //启动
                .verify();
    }
}
