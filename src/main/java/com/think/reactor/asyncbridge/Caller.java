package com.think.reactor.asyncbridge;

import org.springframework.util.StopWatch;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月21日 17:03:00
 */
public class Caller {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("blockingCall start ======================== ");
        blockingCall();
        System.out.println("blockingCall end ======================== ");

        //System.out.println("threadAndCallbackCall start ======================== ");
        //threadAndCallbackCall();
        //System.out.println("threadAndCallbackCall end ======================== ");

        //System.out.println("completableFutureCall start ======================== ");
        //completableFutureCall();
        //System.out.println("completableFutureCall end ======================== ");

        System.out.println("publisherCall start ======================== ");
        publisherCall();
        System.out.println("publisherCall end ======================== ");
    }

    private static void blockingCall() {
        HomePageService homePageService = new HomePageService();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String userInfo = homePageService.getUserInfo();
        System.out.println(userInfo);
        System.out.println(homePageService.getNotice());
        System.out.println(homePageService.getTodos(userInfo));
        stopWatch.stop();
        System.out.println("call methods costs " + stopWatch.getTotalTimeMillis() + " mills");
    }

    private static void threadAndCallbackCall() throws InterruptedException {
        //用于让调用者线程等待多个异步任务全部结束
        CountDownLatch ct = new CountDownLatch(3);
        HomePageService homePageService = new HomePageService();
        HomePageServiceThreadsAndCallbackWrapper homePageServiceThreadsAndCallbackWrapper
                = new HomePageServiceThreadsAndCallbackWrapper(homePageService);
        //统一的finallyCallback
        Runnable finallyCallback = () -> {
            ct.countDown();
        };
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        //获取用户信息
        homePageServiceThreadsAndCallbackWrapper.getUserInfoAsync(
                (userInfo) -> {
                    System.out.println(userInfo);
                    //由于获取todo依赖于用户信息，必须在此处调用
                    homePageServiceThreadsAndCallbackWrapper.getTodos(userInfo,
                            (todos) -> {
                                System.out.println(todos);
                            }, System.err::println, finallyCallback);
                }, System.err::println, finallyCallback
        );
        //获取notice
        homePageServiceThreadsAndCallbackWrapper.getNoticeAsync(System.out::println, System.err::println, finallyCallback);
        //等待异步操作全部结束并统计耗时
        ct.await();
        stopWatch.stop();
        System.out.println("thread and callback async call methods costs " + stopWatch.getTotalTimeMillis() + " mills");
        //退出JVM线程，触发HomePageServiceThreadsAndCallbackWrapper中线程池的shutdownHook
        System.exit(0);
    }

    private static void completableFutureCall() throws InterruptedException {
        //用于让调用者线程等待多个异步任务全部结束
        CountDownLatch ct = new CountDownLatch(2);
        HomePageService homePageService = new HomePageService();
        HomePageServiceCompletableFutureWrapper homePageServiceWrapper =
                new HomePageServiceCompletableFutureWrapper(homePageService);
        //统一的finallyCallback
        Runnable finallyCallback = () -> {
            ct.countDown();
        };
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        homePageServiceWrapper
                .getUserInfoAsync()
                //依赖调用
                .thenCompose(userInfo -> {
                    System.out.println(userInfo);
                    return homePageServiceWrapper.getTodosAsync(userInfo);
                })
                .thenAcceptAsync(System.out::println)
                .thenRun(finallyCallback);

        homePageServiceWrapper
                .getNoticeAsync()
                .thenAcceptAsync(System.out::println)
                .thenRun(finallyCallback);
        //等待异步操作全部结束并统计耗时
        ct.await();
        stopWatch.stop();
        System.out.println("CompletableFuture async call methods costs " + stopWatch.getTotalTimeMillis() + " mills");
    }

    private static void publisherCall() throws InterruptedException {
        //由于调用者线程等待多个异步任务全部结束
        CountDownLatch ct = new CountDownLatch(2);
        //统一的finallyCallback
        Runnable finallyCallback = () -> {
            ct.countDown();
        };
        StopWatch stopWatch = new StopWatch();
        HomePageService homePageService = new HomePageService();
        HomePageServicePublisherWrapper serviceWrapper = new HomePageServicePublisherWrapper(homePageService);
        serviceWrapper.getUserInfoAsync()
                //由于初始化线程池很耗时,所以将StopWatch放置到此处
                //真实系统中，线程池应该提前初始化，而不应该用于一次性的方法
                .doOnSubscribe(subscription -> {
                    stopWatch.start();
                })
                //消费userInfo
                .doOnNext(System.out::println)
                //调用依赖于userInfo的getTodos
                .flatMap(userInfo -> serviceWrapper.getTodosAsync(userInfo))
                //消费todos
                .doOnNext(System.out::println)
                .doFinally(s -> finallyCallback.run())
                .subscribe();

        serviceWrapper
                .getNoticeAsync()
                .doOnNext(System.out::println)
                .doFinally(s -> {
                    finallyCallback.run();
                })
                .subscribe();
        ct.await();
        stopWatch.stop();
        System.out.println("Publisher async call methods costs " + stopWatch.getTotalTimeMillis() + " mills");
    }
}
