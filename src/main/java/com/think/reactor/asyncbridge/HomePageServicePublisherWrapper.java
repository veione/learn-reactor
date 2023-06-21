package com.think.reactor.asyncbridge;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月21日 17:05:00
 */
public class HomePageServicePublisherWrapper {
    private final HomePageService homePageService;
    //线程池
    private Scheduler executor = Schedulers.boundedElastic();

    public HomePageServicePublisherWrapper(HomePageService homePageService) {
        this.homePageService = homePageService;
    }

    public Mono<String> getUserInfoAsync() {
        return Mono
                .fromCallable(this.homePageService::getUserInfo)
                .subscribeOn(this.executor);
    }

    public Mono<String> getNoticeAsync() {
        return Mono
                .fromCallable(this.homePageService::getNotice)
                .subscribeOn(this.executor);
    }

    public Mono<String> getTodosAsync(String userInfo) {
        return Mono
                .fromCallable(() -> this.homePageService.getTodos(userInfo))
                .subscribeOn(this.executor);
    }
}
