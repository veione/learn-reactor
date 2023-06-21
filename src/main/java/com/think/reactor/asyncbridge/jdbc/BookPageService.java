package com.think.reactor.asyncbridge.jdbc;

import org.springframework.util.StopWatch;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月21日 16:48:00
 */
public class BookPageService {
    private static Consumer<Book> bookConsumer = book -> System.out.println("\t" + book);
    private static Consumer<Author> authorConsumer = author -> System.out.println("\t" + author);

    public static void main(String[] args) throws InterruptedException {
        //初始化数据
        H2DataSource.getInstance();
        initScheduler();
        getPage();
        getPageAsync();
    }

    private static void getPage() {
        System.out.println("----------------start get page----------------");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        getAuthors();
        getBooks(bookConsumer);
        stopWatch.stop();
        System.out.println("getPage costs " + stopWatch.getTotalTimeMillis() + " mills");
    }

    private static void getAuthors() {
        AuthorRepository authorRepository = new AuthorRepository();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        authorRepository.getAllAuthors().stream().forEach(authorConsumer);
        stopWatch.stop();
        System.out.println("\tgetAuthors costs " + stopWatch.getTotalTimeMillis() + " mills");
    }

    private static void getBooks(Consumer<Book> consumer) {
        BookRepository bookRepository = new BookRepository();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        bookRepository.getAllBooks().stream().forEach(bookConsumer);
        stopWatch.stop();
        System.out.println("\tgetBooks costs " + stopWatch.getTotalTimeMillis() + " mills");
    }

    private static void getPageAsync() throws InterruptedException {
        System.out.println("----------------start get page async----------------");
        CountDownLatch countDownLatch = new CountDownLatch(2);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        AuthorAsyncRepository authorAsyncRepository = new AuthorAsyncRepository();
        Flux<Author> authorFlux = authorAsyncRepository
                .getAllAuthorsAsync().doOnComplete(() -> countDownLatch.countDown());
        authorFlux.subscribe(authorConsumer);

        BookAsyncRepository bookAsyncRepository = new BookAsyncRepository();
        Flux<Book> flux = bookAsyncRepository
                .getAllBooksAsync().doOnComplete(() -> countDownLatch.countDown());
        flux.subscribe(bookConsumer);
        //等待异步方法都完成
        countDownLatch.await();
        stopWatch.stop();
        System.out.println("getPage costs " + stopWatch.getTotalTimeMillis() + " mills");
    }

    private static void getAuthorAsync() {
        initScheduler();
        AuthorAsyncRepository repository = new AuthorAsyncRepository();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Flux<Author> flux = repository.getAllAuthorsAsync();
        flux.subscribe(authorConsumer);
        System.out.println("\tAsync call started");
        flux.blockLast();
        stopWatch.stop();
        System.out.println("\tgetAuthorAsync cost " + stopWatch.getTotalTimeMillis() + " mills");
    }

    private static void getBooksAsync(Consumer<Book> consumer) {
        initScheduler();
        BookAsyncRepository repository = new BookAsyncRepository();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Flux<Book> flux = repository.getAllBooksAsync();
        flux.subscribe(bookConsumer);
        System.out.println("\tAsync call started");
        flux.blockLast();
        stopWatch.stop();
        System.out.println("\tgetBooksAsync cost " + stopWatch.getTotalTimeMillis() + " mills");
    }

    /**
     * Flux的parallel Scheduler初始化较慢,单独出来,在使用之前初始化
     * parallel单例的，所以此处初始化之后，后面直接用
     */
    private static void initScheduler() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Flux<String> stringFlux = Flux.just("1").subscribeOn(Schedulers.parallel());
        stringFlux.subscribe();
        stringFlux.blockLast();
        stopWatch.stop();
        System.out.println("initScheduler costs " + stopWatch.getTotalTimeMillis() + " mills");
    }
}
