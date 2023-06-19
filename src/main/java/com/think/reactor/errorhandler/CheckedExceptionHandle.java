package com.think.reactor.errorhandler;

import reactor.core.Exceptions;
import reactor.core.publisher.Flux;

import java.io.FileNotFoundException;

/**
 * 非受检查异常会被Reactor传播
 * 为了让受检查异常被reactor的异常传播机制和异常处理机制支持，可以支持如下步骤处理：
 * 1.使用Exceptions.propagate将受检查异常包装为非受检查异常并重新传播出去
 * 2. onError、error回调等异常处理操作获取到异常之后，可以调用Exceptions.unwrap取得原受检的异常
 *
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 19:42:00
 */
public class CheckedExceptionHandle {

    public static void main(String[] args) {
        Flux<String> flux = Flux.just("abc", "def", "exception", "ghi")
                .map(s -> {
                    try {
                        return doSth(s);
                    } catch (FileNotFoundException e) {
                        //包装并传播异常
                        throw Exceptions.propagate(e);
                    }
                });
        //abc、def正常打印，然后打印 参数异常
        flux.log().subscribe(System.out::println,
                e -> {
            //获取原始受检异常
                    Throwable sourceEx = Exceptions.unwrap(e);
                    //推断异常类型并处理
                    if (sourceEx instanceof FileNotFoundException) {
                        System.err.println(sourceEx.getMessage());
                    } else {
                        System.err.println("Other exception");
                    }
                });
    }

    public static String doSth(String str) throws FileNotFoundException {
        if ("exception".equals(str)) {
            throw new FileNotFoundException("参数异常");
        } else {
            return str.toLowerCase();
        }
    }
}
