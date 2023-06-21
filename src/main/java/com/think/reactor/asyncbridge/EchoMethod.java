package com.think.reactor.asyncbridge;

import java.util.concurrent.TimeUnit;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月21日 17:02:00
 */
public class EchoMethod {
    /**
     * 模拟阻塞方法
     *
     * @param str
     * @param delay
     * @param timeUnit
     * @return
     */
    public static String echoAfterTime(String str, int delay, TimeUnit timeUnit) {
        try {
            timeUnit.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return str;
    }
}
