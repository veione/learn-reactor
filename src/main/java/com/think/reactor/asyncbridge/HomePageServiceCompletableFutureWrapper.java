package com.think.reactor.asyncbridge;

import java.util.concurrent.CompletableFuture;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月21日 17:04:00
 */
public class HomePageServiceCompletableFutureWrapper {
    private final HomePageService homePageService;

    public HomePageServiceCompletableFutureWrapper(HomePageService homePageService) {
        this.homePageService = homePageService;
    }

    CompletableFuture<String> getUserInfoAsync() {
        return CompletableFuture.supplyAsync(this.homePageService::getUserInfo);
    }

    CompletableFuture<String> getNoticeAsync() {
        return CompletableFuture.supplyAsync(this.homePageService::getNotice);
    }

    CompletableFuture<String> getTodosAsync(String userInfo) {
        return CompletableFuture.supplyAsync(() -> this.homePageService.getTodos(userInfo));
    }
}
