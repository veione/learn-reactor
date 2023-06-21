package com.think.reactor.asyncbridge;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月21日 17:05:00
 */
public class HomePageServiceThreadsAndCallbackWrapper {
    private final HomePageService homePageService;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
    );

    public HomePageServiceThreadsAndCallbackWrapper(HomePageService homePageService) {
        this.homePageService = homePageService;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> threadPool.shutdownNow()));
    }

    void getUserInfoAsync(Consumer<String> successCallback, Consumer<Throwable> errorCallback, Runnable finallyCallback) {
        threadPool.submit(() -> {
            try {
                String userInfo = this.homePageService.getUserInfo();
                successCallback.accept(userInfo);
            } catch (Throwable ex) {
                errorCallback.accept(ex);
            } finally {
                finallyCallback.run();
            }

        });
    }

    void getNoticeAsync(Consumer<String> successCallback, Consumer<Throwable> errorCallback, Runnable finallyCallback) {
        threadPool.submit(() -> {
            try {
                String notice = this.homePageService.getNotice();
                successCallback.accept(notice);
            } catch (Throwable ex) {
                errorCallback.accept(ex);
            } finally {
                finallyCallback.run();
            }

        });
    }

    void getTodos(String userInfo, Consumer<String> successCallback, Consumer<Throwable> errorCallback, Runnable finallyCallback) {
        threadPool.submit(() -> {
            try {
                String todos = this.homePageService.getTodos(userInfo);
                successCallback.accept(todos);
            } catch (Throwable ex) {
                errorCallback.accept(ex);
            } finally {
                finallyCallback.run();
            }

        });
    }
}
