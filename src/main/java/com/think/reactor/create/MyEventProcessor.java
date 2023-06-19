package com.think.reactor.create;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 11:27:00
 */
interface MyEventProcessor {
    void register(MyEventListener<String> eventListener);

    void fireEvents(String... values);

    void processComplete();

    void shutdown();
}
