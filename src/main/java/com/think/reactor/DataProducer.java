package com.think.reactor;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 10:02:00
 */
@FunctionalInterface
public interface DataProducer<T> {

    T produce();
}
