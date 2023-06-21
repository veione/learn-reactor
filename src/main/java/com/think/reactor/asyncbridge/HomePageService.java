package com.think.reactor.asyncbridge;

import java.util.concurrent.TimeUnit;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月21日 17:03:00
 */
public class HomePageService {

    public String getUserInfo() {
        return EchoMethod.echoAfterTime("get user info", 50, TimeUnit.MILLISECONDS);
    }

    public String getNotice() {
        return EchoMethod.echoAfterTime("get notices", 50, TimeUnit.MILLISECONDS);
    }

    public String getTodos(String userInfo) {
        return EchoMethod.echoAfterTime("get todos", 100, TimeUnit.MILLISECONDS);
    }

}
