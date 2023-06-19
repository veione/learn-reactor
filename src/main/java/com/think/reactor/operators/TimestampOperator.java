package com.think.reactor.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 19:31:00
 */
public class TimestampOperator {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimestampOperator.class);

    public static void main(String[] args) {
        Flux<Integer> sourceFlux = Flux.range(0, 5)
                .map(integer -> {
                    try {
                        //随机休眠一段时间再返回，增加耗时
                        Thread.sleep((long) (Math.random() * 1000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return integer;
                });

        /**
         * elapsed之后返回Flux<Tuple2<Long, Integer>>，Tuple2.getT1()返回
         * 耗时，Tuple2.getT2()返回数据值
         * 如果使用log，则会打印出信号、当前时钟时间和数据值
         */
        Flux<Tuple2<Long, Integer>> timedFlux = sourceFlux.timestamp();
        LOGGER.info("current clock time is {} ", System.currentTimeMillis());
        timedFlux.log().subscribe();
    }
}
