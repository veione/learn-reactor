package com.think.reactor.create;

import java.util.List;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 11:27:00
 */
interface MyEventListener<T> {
    void onEvents(List<T> chunk);

    void processComplete();
}
