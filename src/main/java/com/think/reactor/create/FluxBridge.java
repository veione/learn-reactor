package com.think.reactor.create;

import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 11:26:00
 */
public class FluxBridge {
    private static MyEventProcessor myEventProcessor = new ScheduledSingleListenerEventProcessor();

    public static void main(String[] args) {
        Flux.create(sink -> {
                    myEventProcessor.register(
                            new MyEventListener<>() {
                                @Override
                                public void onEvents(List<String> chunk) {
                                    for (var s : chunk) {
                                        if ("end".equals(s)) {
                                            sink.complete();
                                            myEventProcessor.shutdown();
                                        } else {
                                            sink.next(s);
                                        }
                                    }
                                }

                                @Override
                                public void processComplete() {
                                    sink.complete();
                                }
                            }
                    );
                })
                .log()
                .subscribe(System.out::println);
        myEventProcessor.fireEvents("abc", "efg", "123", "456", "end");
        System.out.println("main thread exit");
    }
}
