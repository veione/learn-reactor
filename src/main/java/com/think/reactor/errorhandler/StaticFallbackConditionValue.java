package com.think.reactor.errorhandler;

import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 根据抛出的异常类型返回不同的值
 *
 * @author veione
 * @version 1.0.0
 * @date 2023年06月20日 09:54:00
 */
public class StaticFallbackConditionValue {

    public static void main(String[] args) {
        //1.根据异常类型进行判断
        Flux<Integer> flux = Flux.just(0)
                .map(i -> 1 / i)
                //ArithmeticException异常时返回1
                .onErrorReturn(NullPointerException.class, 0)
                .onErrorReturn(ArithmeticException.class, 1);
        //输出应该为1
        flux.log().subscribe(System.out::println);

        final String nullStr = null;
        //just不允许对象为空
        Flux<String> stringFlux = Flux.just("")
                .map(str -> nullStr.toString())
                //NullPointException异常时返回字符串NullPointException
                .onErrorReturn(NullPointerException.class, "NullPointException")
                .onErrorReturn(ArithmeticException.class, "ArithmeticException");
        //输出应该为NullPointException
        stringFlux.log().subscribe(System.out::println);

        //2.根据Predicate进行判断
        AtomicInteger index = new AtomicInteger(0);
        Flux.just(0, 1, 2, 3)
                .map(i -> {
                    index.incrementAndGet();
                    return 1 / i;
                })
                .onErrorReturn(NullPointerException.class, 0)
                .onErrorReturn(e -> index.get() < 2, 1)
                //因为上一个onErrorReturn匹配了条件,所以异常传播被关闭，之后的
                //onErrorReturn不会再被触发
                .onErrorReturn(e -> index.get() < 1, 2)

                //因为异常类型为NumberFormatException,此处应打印1
                .log().subscribe(System.out::println);
    }
}
