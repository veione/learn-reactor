package com.think.reactor;

import java.util.function.Supplier;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 10:15:00
 */
public class DivideInterSupplier implements Supplier<Integer> {
    private final Integer integer1;
    private final Integer integer2;

    public DivideInterSupplier(Integer integer1, Integer integer2) {
        this.integer1 = integer1;
        this.integer2 = integer2;
    }

    @Override
    public Integer get() {
        return integer1 / integer2;
    }
}
