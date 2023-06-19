package com.think.reactor.factory;

import reactor.core.publisher.Flux;

/**
 * range方法指定一个起始数和数量然后生成数据序列
 *
 * @author veione
 * @version 1.0.0
 * @date 2023年06月19日 11:11:00
 */
public class Range {

    public static void main(String[] args) {
        Flux.range(1, 5).subscribe(System.out::println);
        Flux.range(6, 5).subscribe(System.out::println);
    }
}
