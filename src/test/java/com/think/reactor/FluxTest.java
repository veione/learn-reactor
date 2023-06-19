package com.think.reactor;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月16日 16:59:00
 */
public class FluxTest {
    /**
     * Create a {@link Flux} that emits the provided elements and then completes.
     * 创建一个发送给定的元素然后完成的Flux。
     */
    @Test
    public void testJust() {
        Flux.just("foo", "bar", "baz")
                .subscribe(this::consoleConsumer);
    }

    /**
     * Merge data from Publisher sequences provided in an array/vararg into an ordered merged sequence.
     * Unlike concat, sources are subscribed to eagerly.
     * Unlike merge, their emitted values are merged into the final sequence in subscription order.
     * <p>
     * 将数组/可变参数中提供的 Publisher 序列中的数据合并到有序的合并序列中。
     * 与 concat 不同，资源是立即被订阅的。
     * 与合并不同，它们发出的值按订阅顺序合并到最终序列中。
     *
     * @throws InterruptedException
     */
    @Test
    public void testMergeSequential() throws InterruptedException {
        Flux<Integer> flux1 = Flux.just(1, 2, 3).delayElements(Duration.ofSeconds(1));
        Flux<Integer> flux2 = Flux.just(4, 5, 6);
        Flux<Integer> flux3 = Flux.just(7, 8, 9);
        Flux.mergeSequential(flux1, flux2, flux3)
                .subscribe(this::consoleConsumer);

        Thread.sleep(5000);
    }

    /**
     * Create a Flux that terminates with the specified error, either immediately after being subscribed to or after being first requested.
     * <p>
     * 创建一个以指定错误终止的 Flux，要么在订阅后立即终止，要么在首次请求后立即终止。
     */
    @Test
    public void testError() {
        Flux.error(new RuntimeException(), true)
                .subscribe(this::consoleConsumer);

        Flux.error(RuntimeException::new)
                .subscribe(this::consoleConsumer);
    }

    /**
     * Merge data from Publisher sequences contained in an array / vararg into an interleaved merged sequence.
     * Unlike concat, sources are subscribed to eagerly.
     * <p>
     * 将数组/可变参数中包含的 Publisher 序列的数据合并到交错的合并序列中。
     * 与 concat 不同，资源是立刻订阅的。
     * <p>
     * `Flux.merge()` 方法是 Reactor Core 提供的一个方法，用于将多个 Flux 序列合并为一个 Flux 序列，并发出它们的元素。
     * 与 `Flux.zip()` 方法不同的是， `merge()` 方法不会等待每个 Flux 发出所有的元素，而是在它们的元素发出时立即将它们合并，因此也称为并行合并。
     * <p>
     * 常见的使用场景包括：当我们需要将多个事件流合并为一个事件流并发出元素时，例如，我们可能会同时订阅多个消息队列，当它们的消息到达时，
     * 我们需要将它们合并为一个流并对它们进行处理；或者我们可能需要发起多个并发请求，并将它们的结果合并为一个流。
     * <p>
     * 需要注意的是，由于 `merge()` 方法会将多个 Flux 合并为一个流，因此有可能会存在元素交错的情况，即两个或多个 Flux 同时发出元素并交错在一起。
     * 这个问题可以通过 `concat()` 方法来避免，它保证只有前一个 Flux 完成后才会开始处理下一个 Flux。
     */
    @Test
    public void testMerge() throws InterruptedException {
        Flux<Integer> flux1 = Flux.just(1, 2, 3).delayElements(Duration.ofSeconds(1));
        Flux<Integer> flux2 = Flux.just(4, 5, 6);
        Flux<Integer> flux3 = Flux.just(7, 8, 9);

        Flux.merge(flux1, flux2, flux3)
                .subscribe(this::consoleConsumer);

        Thread.sleep(5000);
    }

    /**
     * Create a Flux that emits the items contained in the provided array.
     * 创建一个 Flux 来发射包含在提供的数组中的项目。
     */
    @Test
    public void testFromArray() {
        String[] array = {"foo", "bar", "baz"};
        Flux.fromArray(array)
                .subscribe(this::consoleConsumer);
    }

    /**
     * Decorate the specified Publisher with the Flux API.
     * 使用 Flux API 装饰指定的 Publisher。
     */
    @Test
    public void testFrom() {
        Flux.from(Flux.just("foo", "bar", "baz"))
                .subscribe(this::consoleConsumer);
    }

    /**
     * Create a Flux that emits long values starting with 0 and incrementing at specified time intervals, after an initial delay,
     * on the global timer. If demand is not produced in time,
     * an onError will be signalled with an overflow IllegalStateException detailing the tick that couldn't be emitted.
     * In normal conditions, the Flux will never complete.
     * 创建一个 Flux，它在全局计时器上初始延迟后发出从 0 开始并在指定时间间隔递增的长值。
     * 如果无法发出 tick，则会触发一个 onError并详细说明无法发出 tick 的情况，包括一个溢出的 IllegalStateException。
     * 在正常情况下，Flux 永远不会完成。
     * 在 Schedulers.parallel() 调度程序上运行。
     *
     * <p>
     * Flux.interval(Duration)是 Reactor 的一个方法，用于创建一个可观测序列，该序列会隔一段时间发出一个递增的数字。
     * 具体来说，它会创建一个 Flux 序列，该序列会在每个指定的时间段之后发出一个增加的数字。
     * 这个操作符常用于需要做定时任务的场景。
     * </p>
     *
     * @throws InterruptedException
     */
    @Test
    public void testInterval() throws InterruptedException {
        Flux.interval(Duration.ofSeconds(1), Duration.ofSeconds(1))
                .map(i -> "第 " + (i + 1) + " 次执行任务")
                .subscribe(this::consoleConsumer);
        Thread.sleep(3000);
    }

    /**
     * Create a Flux that will never signal any data, error or completion signal.
     * <p>
     * 创建一个不会发出任何数据，错误或者完成信号的Flux。
     *
     * <p>
     * Flux.never()是 Reactor 的一个方法，用于创建一个永远不会发出任何数据的 Flux 序列。
     * 它只能发出 onSubscribe 事件和 onComplete 事件。具体来说，它创建了一个空的永久性 Flux 序列，
     * 可以用于在一些特定的场景下，如需要等待一些事件的触发，但又不想占用太多资源的情况下。
     * <p>
     * 例如，在使用 Reactor 进行单元测试时，我们可能需要创建一些虚拟的 Flux 序列进行测试。
     * 但是，在某些测试用例中，我们需要测试的是某个异步事件的触发和处理过程，而不是目标方法的返回结果。
     * 这种情况下，我们可以使用 Flux.never() 来创建一个永远不会完成的 Flux 序列，这样就可以模拟异步事件的不停等待，
     * 从而进行测试。
     * </p>
     */
    @Test
    public void testNever() {
        Flux.never()
                .subscribe(this::consoleConsumer);

        Mono<Void> result = Flux.from(Mono.fromRunnable(() -> {
            System.out.println("Do something Async task");
        })).delayElements(Duration.ofSeconds(1)).then();

        StepVerifier.create(result)
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(10)) // 等待一段时间
                .verifyComplete();
    }

    /**
     * Create a Flux that completes without emitting any item.
     * 创建一个不会发射任何数据然后完成的Flux。
     */
    @Test
    public void testEmpty() {
        Flux<Void> flux = Flux.empty();

        StepVerifier.create(flux)
                .expectSubscription()
                .verifyComplete();
    }

    /**
     * Create a Flux that emits the items contained in the provided Stream.
     * Keep in mind that a Stream cannot be re-used, which can be problematic in case of multiple subscriptions or re-subscription (like with repeat() or retry()).
     * The Stream is closed automatically by the operator on cancellation, error or completion.
     * <p>
     * 创建一个从给定Stream容器中发射数据的Flux。
     * 需要注意的是，Stream 不能重复使用，这在多次订阅或重新订阅（如使用 repeat() 或 retry()）的情况下可能会出现问题。
     * 在取消、错误或完成时，操作符会自动关闭流。也就是说，当流被取消订阅，或者发生错误或完成事件时，操作符会自动将流关闭，并释放相关资源。
     */
    @Test
    public void testFromStream() {
        Stream<Integer> stream = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Flux<Integer> flux = Flux.fromStream(stream);

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNextCount(10)
                .verifyComplete();
    }

    /**
     * Create a Flux that emits the items contained in the provided Iterable.
     * The Iterable.iterator() method will be invoked at least once and at most twice for each subscriber.
     * <p>
     * 创建一个Flux然后从给定的Iterable容器中发射数据。
     * Iterable.iterator() 方法将为每个订阅者至少调用一次，最多调用两次。
     */
    @Test
    public void testFromIterable() {
        Flux<Integer> flux = Flux.fromIterable(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNextCount(10)
                .verifyComplete();
    }

    /**
     * Build a Flux that will only emit a sequence of count incrementing integers, starting from start. That is,
     * emit integers between start (included) and start + count (excluded) then complete.
     * <p>
     * 创建一个 Flux，它只会发出一系列递增的整数，从给定的起始值(start)开始。也就是说，发出范围在start（包括）和start + count（不包括）之间的整数，然后终止流。
     */
    @Test
    public void testRange() {
        Flux<Integer> flux = Flux.range(1, 10);

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNextCount(10)
                .verifyComplete();
    }

    /**
     * Concatenate all sources provided as a vararg, forwarding elements emitted by the sources downstream.
     * Concatenation is achieved by sequentially subscribing to the first source then waiting for it to complete before subscribing to the next,
     * and so on until the last source completes.
     * Any error interrupts the sequence immediately and is forwarded downstream.
     * <p>
     * 将提供的多个源（以可变参数的形式）连接起来，将源发出的元素向下游转发。连接是通过顺序订阅第一个源，然后等待其完成，
     * 然后订阅下一个源，依次进行，直到最后一个源完成来实现的。任何错误会立即中断序列，并向下游转发该错误。
     * <p>
     * `Flux.concat()` 方法可以用于将多个序列合并成一个序列并保证它们按顺序执行。它的使用场景通常是在需要按顺序执行多个异步操作的情况下。
     * 例如，当需要执行一个Ajax请求后，紧接着执行另一个Ajax请求，并且必须要按顺序执行这两个请求，同时需要将两个请求的数据拼接在一起时，
     * 可以使用`Flux.concat()` 方法将这两个请求合并成一个序列来处理。
     * <p>
     * 另一个应用场景是在需要在两个或多个事件序列中按顺序执行操作时对它们进行合并。例如，当需要在两个或多个延迟序列中按顺序调用某个方法时，
     * 可以使用 `Flux.concat()` 方法将这些序列合并成一个序列，并在该序列上注册相应的回调来处理它们。
     * <p>
     * 总之，`Flux.concat()` 方法通常用于需要按顺序执行多个异步或事件操作的场景。
     */
    @Test
    public void testConcat() {
        Flux<Integer> flux = Flux.concat(Flux.just(1, 2, 3).delayElements(Duration.ofSeconds(1)), Flux.just(4, 5, 6), Flux.just(7, 8, 9));

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNextCount(9)
                .verifyComplete();
    }

    /**
     * Build a Flux whose data are generated by the combination of the most recently published value from each of two Publisher sources.
     * 构建一个 Flux，它的数据由两个 Publisher 源中最近发布的值组合生成。
     *
     * <p>
     * Reactor库的`Flux.combineLatest()`方法用于在多个数据源流中的任何一个流发出元素时，聚合这些流的最新元素并将它们合并到一起。下面是一些使用规律：
     * 1. `Flux.combineLatest(Flux1, Flux2, Flux3)`可以将多个Flux对象进行转换。例如，给定3个数据源流`Flux1`、`Flux2`和`Flux3`，
     * 当其中任何一个流发出元素时，`combineLatest()`会收到所有流的最新元素，并将它们合并。这个合并的结果是一个元素数组。
     * 例如，当`Flux1`发出"1"和"2", `Flux2`发出"A"和"B"，而 `Flux3`发出"@"，那么返回的结果会是`{"2", "A", "@"}`和`{"2", "B", "@"}`。
     * <p>
     * 2. `Flux.combineLatest()`方法可以支持多种不同类型的数据源流。例如，你可以将`Flux`与`Mono`、`Completable`和`Single`等类型的流进行组合。
     * <p>
     * 3. `Flux.combineLatest()`方法的返回结果类型是`Flux`，这个`Flux`会在每次数据源流发出元素时都进行更新。这个新的Flux流将包含每个数据源流的最新元素。这样，你可以使用`subscribe()`方法对这个Flux流进行订阅，并获取每个数据源流的最新值的更新。
     * <p>
     * 总之，`Flux.combineLatest()`方法可以用于将多个数据源流的最新数据合并到一起。这样就可以将多个源的数据组合在一起，并同时处理这些数据的更改。
     * </p>
     */
    @Test
    public void testCombineLatest() {
        Flux.combineLatest(Flux.just(1, 2, 30), Flux.just(10, 20, 30), (t1, t2) -> t1 + t2)
                .log()
                .subscribe(this::consoleConsumer);

        Flux<Integer> flux = Flux.combineLatest(Flux.just(1, 2, 3), Flux.just(4, 5, 6), (t1, t2) -> t1 + t2);

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();
    }

    /**
     * Concatenate all sources provided as a vararg, forwarding elements emitted by the sources downstream.
     * Concatenation is achieved by sequentially subscribing to the first source then waiting for it to complete before subscribing to the next,
     * and so on until the last source completes.
     * Errors do not interrupt the main sequence but are propagated after the rest of the sources have had a chance to be concatenated.
     * 将所有提供的源作为可变参数连接起来，向下游转发源发出的元素。连接是通过先顺序订阅第一个源，
     * 等待其完成后再订阅下一个源，依此类推直到最后一个源完成来实现的。
     * 错误不会打断主序列，但会在其余的源连接完成之后传播。
     *
     * <p>
     * Flux.concatDelayError()是一个操作符，用于将多个源Flux依次拼接为一个大的序列，如果其中某一个源Flux出现错误，则会将该错误推迟到序列结束后才发布。
     * 这个操作符的使用场景通常在收集多个数据源的时候，如果某一个数据源出现了错误，但是我们仍希望能够接收后续的数据，而不是直接停止流。
     * </p>
     */
    @Test
    public void testConcatDelayError() {
        Flux<Integer> flux = Flux.concatDelayError(Flux.just(1, 2, 3), Flux.error(RuntimeException::new), Flux.just(7, 8, 9));

        StepVerifier.create(flux)
                .expectSubscription()
                .expectError(RuntimeException.class);

        Flux<Integer> api1 = Flux.just(1, 2, 3);
        Flux<Integer> api2 = Flux.just(4, 5);
        Flux<Integer> api3 = Flux.just(6, 7, 8, 9);

        // 模拟一个错误的数据源
        Flux<Integer> errorSource = Flux.error(new RuntimeException("Error happened in API3"));

        // 将多个数据源依次拼接为一个序列
        Flux<Integer> result = Flux.concatDelayError(api1, api2, errorSource, api3);

        result.subscribe(
                System.out::println,
                error -> System.out.println("Got Error: " + error.getMessage()),
                () -> System.out.println("Completed")
        );
    }

    /**
     * Programmatically create a Flux with the capability of emitting multiple elements in a synchronous or asynchronous manner
     * through the FluxSink API. This includes emitting elements from multiple threads.
     * 编写一个利用FluxSink API创建一个Flux流，使其能够以同步或异步的方式发出多个元素，包括从多个线程发出元素。
     *
     * <p>
     * `Flux.create()` 方法适用于以下场景：
     * <p>
     * 1. 需要手动控制生成元素的情况。
     * 2. 需要在一个不支持反压的异步序列中手动处理反压及其缓冲区的情况。
     * 3. 需要在基于事件的系统中生成复杂的序列，例如从 WebSocket 或其它异步通道接收消息。
     * <p>
     * 在这些情况下，Flux.create() 方法可以提供对 FluxSink API 的访问，该 API 允许手动处理元素的生成和反压。
     * 但是需要注意的是，由于对元素生成和反压的控制权完全在开发者手中，因此使用 Flux.create() 的时候一定要小心，
     * 避免产生线程安全问题和内存泄漏等问题。
     * </p>
     */
    @Test
    public void testCreate() {
        Flux<String> flux = Flux.create(sink -> {
            // 创建一个 CompletableFuture 对象
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                // 模拟一个耗时的异步任务
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "hello";
            });

            // 注册异步任务完成时的回调函数
            future.whenComplete((result, error) -> {
                if (error != null) {
                    sink.error(error); // 发出错误信号
                } else {
                    sink.complete(); // 发出完成信号
                }
            });
        });

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext()
                .verifyComplete();
    }

    /**
     * Lazily supply a Publisher every time a Subscription is made on the resulting Flux,
     * so the actual source instantiation is deferred until each subscribe and the Supplier can create a subscriber-specific instance.
     * If the supplier doesn't generate a new instance however, this operator will effectively behave like from(Publisher).
     * <p>
     * 每次在所得的Flux上进行订阅时，都会惰性地提供一个Publisher，因此实际的数据源实例化被推迟到每个订阅，函数可以创建一个订阅者特定的实例。
     *
     * <p>
     * `Flux.defer()` 方法适用于以下场景：
     * 1. 当源序列需要延迟生成，比如需要等待某些资源就绪。
     * 2. 当每次订阅时，需要生成一个新的数据流，而不是共享同一个数据流。
     * 3. 当需要在订阅时检查一些条件，只有满足条件时才能开始数据流的生成。
     * <p>
     * 在这些情况下，`Flux.defer()` 方法可以提供一个延迟生成数据流的能力，并在每次订阅时生成一个新的数据流。
     * 这个方法需要一个函数作为参数，返回一个 Publisher 对象，以进行订阅。
     * 函数仅在订阅时执行，因此允许开发者在此时控制数据流的生成，从而实现"延迟生成"的效果。
     * </p>
     */
    @Test
    public void testDefer() {
        Flux<Integer> flux = Flux.defer(() -> Flux.just(1, 2, 3, 4, 5, 6));

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNextCount(6)
                .verifyComplete();
    }

    /**
     * 每次在所得的Flux上进行订阅时，都会惰性地提供一个Publisher，因此实际的数据源实例化被推迟到每个订阅，函数可以创建一个订阅者特定的实例。
     * 该操作符与`defer(Supplier)`的行为相同，但接受一个函数作为参数，该函数将当前的ContextView作为参数。但是，如果该函数没有生成一个新的实例，
     * 那么这个操作符将有效地像from（Publisher）那样行为。
     * 如果需要在订阅时生成对当前上下文视图的依赖关系，请使用此操作符。
     */
    @Test
    public void testDeferContextual() {
        Flux<Integer> flux = Flux.deferContextual(ctx -> {
            return Flux.just(1, 2, 3, 4, 5, 6);
        });

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNextCount(6)
                .verifyComplete();
    }

    /**
     * 从多个 Publisher 中挑选第一个发出任何信号（onNext/onError/onComplete）的 Publisher，并重放来自该 Publisher 的所有信号，
     * 实际上表现为这些竞争源中的最快源。该操作符会选择最先发射信号的 Publisher，忽略其余的 Publisher，并将所选 Publisher 发出的所有信号进行重放。
     * 因此，该操作符实际上表现为这些竞争源中的最快源。
     *
     * <p>
     * Flux.firstWithSignal()用于在多个流中选择最先有信号（onNext/onError/onComplete）的流，并将其作为新的Flux进行订阅。
     * 该操作符能够获取多个数据流并返回一个新的数据流，该新数据流只会发出第一个发出信号的原始数据流的所有信号。
     * <p>
     * 这个操作符的使用场景是，当您需要观察多个数据流，并在第一个数据流发出信号时，继续处理这个数据流的信号，而忽略其他数据流时，可以使用该操作符。
     * 例如，您可以将多个传感器数据流合并在一起，当其中任何一个传感器发出数据时，选择相应的流进行处理，而忽略其他传感器的数据。
     * </p>
     */
    @Test
    public void testFirstWithSignal() {
        Flux.firstWithSignal(Flux.just(1, 2, 3).delayElements(Duration.ofSeconds(1)), Flux.just(4, 5, 6))
                .log()
                .subscribe(this::consoleConsumer);

        Flux<Integer> flux = Flux.firstWithSignal(Flux.just(1, 2, 3), Flux.just(4, 5, 6));

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();
    }

    /**
     * 挑选第一个发射任何值的 Publisher，并重放来自该 Publisher 的所有值，类似于在第一个发出 onNext 的源上操作。
     * 在多个源中，带有值的源始终“胜过”空源（只发出 onComplete）或失败的源（只发出 onError）。
     * 当没有任何源能够提供值时，此操作符将失败并抛出 NoSuchElementException（如果至少有两个源）。
     * 此异常的 cause 是一个组合异常，可用于检查每个源的错误信息（因此，组合异常中的元素数量等于源的数量）。
     * 来自失败源的异常直接反映在组合异常中的失败源的索引上。对于空源，会在其相应的索引处添加 NoSuchElementException。
     * 可以使用 Exceptions.unwrapMultiple(topLevel.getCause()) 将这些错误轻松地查看为 List。
     * 请注意，与 firstWithSignal(Publisher[]) 中的情况类似，如果没有其他源发出 onNext，则无限源可能会有问题。
     * 如果第一个源已经是基于数组的 firstWithValue(Publisher, Publisher[]) 实例，
     * 则避免了嵌套：创建了一个包含 first 中的所有源以及同一级别的所有其他源的新的基于数组的实例。
     *
     * <p>
     * Flux.firstWithValue() 用于从多个数据源中获取第一个发出数据的数据源，然后订阅该数据源，并忽略其他数据源。
     * 该操作符可用于处理多个数据源的情况，并在只需要处理第一个发出数据的数据源时选择其中一个数据源进行订阅。
     * 例如，您可能有多个生产者（Producer），每个生产者都会发出某种类型的数据。但是，您只需要将第一个接收到的数据传递给下一个处理器（Processor），
     * 忽略其他生产者发出的所有数据。在这种情况下，您可以使用 Flux.firstWithValue() 操作符来选择第一个发出数据的生产者进行订阅。
     * 另一个使用场景是在从网络或其他外部资源获取数据时，您不确定哪个源将首先发出数据。在这种情况下，您可以使用 Flux.firstWithValue() 操作符来订阅多个数据源，
     * 并根据第一个发出数据的数据源的数据进行处理。
     * </p>
     */
    @Test
    public void testFirstWithValue() {
        Flux.firstWithValue(Flux.just(1, 2, 3).delayElements(Duration.ofSeconds(1)), Flux.just(4, 5, 6))
                .log()
                .subscribe(this::consoleConsumer);
    }

    /**
     * Programmatically create a Flux by generating signals one-by-one via a consumer callback and some state, with a final cleanup callback.
     * The stateSupplier may return null but your cleanup stateConsumer will need to handle the null case.
     * <p>
     * 通过使用消费者回调和一些状态逐个生成信号，最终通过清理回调方法创建 Flux。stateSupplier 方法可能返回 null，但您的清理 stateConsumer 方法需要处理这种情况。
     * 换句话说，此方法允许您以编程方式逐个生成 Flux 中的信号，并在处理完所有信号后执行某些清理操作。
     * 在生成信号的过程中，使用的状态可以供 stateSupplier 方法生成，并通过 stateConsumer 方法在清理期间进行处理。
     * 需要注意的是，如果 stateSupplier 返回 null，则您必须确保 stateConsumer 可以处理 null 值。
     *
     * <p>
     * `Flux.generate()` 方法可以用于根据生成器函数生成信号来创建 Flux。它允许您以编程方式控制生成的信号，并返回一个 Disposable 对象以检索创建的发布者。
     * 以下是一些适合使用 `Flux.generate()` 方法的场景：
     * - 需要从某些状态或条件中生成无限数量的值。
     * - 需要模拟某些吞吐量，例如从本地文件系统读取或从远程服务获取数据。
     * - 需要记录生成的所有值以进行调试、测试或其他目的。
     * - 需要动态生成数据的情况。例如，根据某些事件或条件，仅在某些变量中存在时生成数据。
     * 总的来说， `Flux.generate()` 适合需要对数据流进行高度控制的场景。可能需要编写更多的代码来实现所需的逻辑，但是这对于需要细粒度控制的场景可能是必要的。
     * </p>
     */
    @Test
    public void testGenerate() {
        Flux.generate(() -> 1, (state, sink) -> {
                    sink.next(state);
                    if (state == 10) {
                        sink.complete();
                    }
                    System.out.println(state + ", sink = " + sink);
                    return state + 1;
                }, stateConsumer -> {
                    System.out.println("consumer : " + stateConsumer.intValue());
                })
                .log()
                .subscribe(this::consoleConsumer);
    }

    /**
     * 将提供的Publisher序列中的数据合并到一个有序的合并序列中，通过从每个源中选择最小的值（根据它们的自然顺序定义）。
     * 这不是一个sort()，因为它不考虑每个序列的全部内容。
     * 相反，此操作符仅考虑每个源的一个值，并选择所有这些值中最小的值，然后为选择的源填充该插槽。
     *
     * <p>
     * `Flux.mergeComparing()` 方法用于将多个 Publisher 中的数据合并为一个单独的 Ordered Merged Sequence（有序合并序列），它会选择源序列中小的值，并根据其自然顺序排列。这个操作不会对整个序列进行排序，它只会从每个源序列中选择一个值，并选择所有这些值中最小的一个，然后更新该选定源序列的值。
     * <p>
     * 以下是一些适合使用 `Flux.mergeComparing()` 方法的场景：
     * <p>
     * - 将多个数据源中的数据合并为一个有序的流。这些源可以是来自不同数据库、文件或其他位置的数据源。
     * - 对于多个排序的数据源，您可以将它们合并为一个有序的序列，而不需要显式排序。
     * - 如果需要对多个数据源的数据进行复杂的比较，则可以编写自定义比较器，并将其提供给 `Flux.mergeComparing()` 方法，以便进行排序和合并。
     * <p>
     * 总的来说， `Flux.mergeComparing()` 适合需要将多个数据源的数据合并为一个有序序列的场景，同时还支持自定义比较器以进行数据比较。
     * 它通常比 `Flux.merge()` 更适合需要完全控制合并过程的情况。
     * </p>
     */
    @Test
    public void testMergeComparing() {
        Flux.mergeComparing(Flux.just(1, 2, 3), Flux.just(4, 5, 6))
                .log()
                .subscribe(this::consoleConsumer);
    }

    /**
     * 将提供的Publisher序列中的数据合并成一个按顺序排列的序列，每次从各个源中选取最小的值（根据提供的比较器定义），这不是一个sort(Comparator)（排序）操作，
     * 因为它不考虑每个序列的整体。
     * 相反，这个操作只考虑来自每个源的一个值，并选择所有这些值中最小的，然后重新填充选择的源的插槽。
     * 请注意，它会延迟错误直到所有数据被消耗完毕。
     *
     * <p>
     * Flux.mergeComparingDelayError()可以用于将多个Flux序列合并为一个Flux序列，并以升序方式将事件流序列化。当一个或多个Flux序列遇到错误时，
     * 此操作符会等待所有序列已消耗完之后再抛出错误，保证错误不会中断数据流并确保所有事件已被消费。
     * <p>
     * 例如，假设有三个Flux序列a、b、c，它们产生的事件流是[1, 3, 5]、[2, 4, 6]和[7, 8, 9]，那么使用Flux.mergeComparingDelayError()操作符将这三个序列合并，
     * 可以得到如下的事件序列[1, 2, 3, 4, 5, 6, 7, 8, 9]。
     * <p>
     * 当其中一个信号源出现错误时，将忽略该信号源，直到其他信号源产生完成之后，再将错误通知给订阅者。这种情况下，订阅者将会接收到一个CompositeException例外，
     * 其中包括了所有因错误而导致的异常。因此，Flux.mergeComparingDelayError()操作符适用于即使某些信号源无法正常运行，仍需要确保其他信号源顺序产生结果的情况。
     * </p>
     */
    @Test
    public void testMergeComparingDelayError() {
        Flux.mergeComparingDelayError(1, Comparator.comparingInt(o -> o), Flux.just(1, 2, 3), Flux.error(new RuntimeException()), Flux.just(4, 5, 6))
                .log()
                .subscribe(this::consoleConsumer);
    }

    /**
     * 将提供的Publisher序列中的数据合并成一个按顺序排列的序列，每次从各个源中选取最小的自然顺序值，即当它们到达时。
     * 这不是一个sort()操作，因为它不考虑每个序列的整体。与mergeComparing不同，此操作符也不等待每个源都有一个值到达。
     * 虽然这个操作符最多从每个源中检索一个值，但它只在两个或更多的源在同一时间发出时才进行比较。
     * 在这种情况下，它选择这些竞争值中最小的一个，并在有需求时继续选择最小值。因此，它最适合于异步源，其中不希望在向下游发射值之前等待每个源的值。
     *
     * <p>
     * Flux.mergePriority()可以用来合并多个Flux序列，并加入优先级机制。Flux.mergePriority()操作符可以将不同Flux序列的元素按照给定的优先级进行合并，
     * 合并后的元素将按照它们在各个Flux序列内的优先级进行排序输出。
     * 在某些场景下，需要并发的处理多个消息队列或数据源，按照优先级顺序来输出这些数据。例如，需要处理两个数据源 `A` 和 `B`，A 的数据优先级为高（值为1），
     * B 的数据优先级为低（值为2）。如果 `A `和 `B` 同时都有数据到达，`A`的数据就应优先于 `B` 的数据输出。这个时候可以使用Flux.mergePriority()，
     * 并指定优先级为1的是 `A` 源，优先级为2的是 `B` 源。这样，如果 `A` 和 `B` 同时发出元素， `A` 的元素将被先输出。如果此时 `B` 源发出更多元素，
     * 那么这些元素也会按照优先级依次输出。
     * 因此，Flux.mergePriority()操作符适用于需要考虑元素优先级顺序的场景，能够保证高优先级的元素优先输出。
     * </p>
     */
    @Test
    public void testMergePriority() {
        Flux<Integer> sourceA = Flux.just(4, 7, 2, 5); // 高优先级源A
        Flux<Integer> sourceB = Flux.just(9, 8, 3, 1); // 低优先级源B

        Flux<Integer> orderedMerge = Flux.mergePriority(
                Integer::compareTo, // 比较函数，指定优先级
                sourceA, sourceB
        );

        orderedMerge.log().subscribe(this::consoleConsumer);
    }

    /**
     * 将提供的Publisher序列中的数据按照指定的Comparator选择最小的数据，按照顺序合并为一个序列。该操作不同于sort(Comparator)，
     * 因为它不会考虑每个序列中的全部元素。与mergeComparing不同的是，该操作符在等待至少一个源的值到达之后不再等待。
     * 虽然此操作符最多会从每个源中检索一个值，但它仅在两个或多个源同时发出值时才进行比较。在这种情况下，它选择这些竞争值中最小的一个，
     * 并在存在需求时继续这样做。因此，它最适合用于异步源的场景，在那里您不想等待每个源的值就可以向下游发出值。
     * 请注意，它会延迟错误，直到所有数据都被消耗完。
     */
    @Test
    public void testMergePriorityDelayError() {
        Flux<Integer> sourceA = Flux.just(4, 7, 2, 5); // 高优先级源A
        Flux<Integer> sourceB = Flux.just(9, 8, 3, 1); // 低优先级源B
        Flux<Integer> error = Flux.error(RuntimeException::new); //异常

        Flux<Integer> orderedMerge = Flux.mergePriorityDelayError(
                1,
                Integer::compareTo, // 比较函数，指定优先级
                sourceA, sourceB, error
        );

        orderedMerge.log().subscribe(this::consoleConsumer);
    }

    /**
     * 通过FluxSink API，以程序控制的方式创建一个Flux对象，使它能够从单线程生产者中发出多个元素。
     * 对于多线程支持的可选方案，请参见create(Consumer, FluxSink.OverflowStrategy)方法。
     *
     * <p>
     * `Flux.push()`方法可以用于在生产数据时进行手动推送，而不是等待订阅者订阅后再推送数据。这使得生产者可以在生产数据时进行更多的控制，而不必等待被消耗完的数据的信号。 这样可以带来更好的流控制，避免订阅者在不需要数据时占用过多资源。
     * 使用场景：
     * 1. 当生产者需要手动控制生产速度时，可以使用`Flux.push()`方法。这对于需要针对生产者的速度进行优化的场景非常有用。
     * 2. 如果生产数据的操作本身是阻塞式的，即在生成每个数据时需要等待IO或其他操作完成，那么可以使用`Flux.push()`将数据推送到流中，而不是等待订阅者处理所有数据。
     * 3. 当数据源可以从多个位置发出数据时，可以使用`Flux.push()`方法来手动发出数据。这对于需要从不同的源收集数据的场景非常有用。
     * 需要注意的是，使用`Flux.push()`方法时，需要手动控制流控制，以避免对下游应用程序造成负面影响，例如内存泄露或资源浪费。
     * </p>
     */
    @Test
    public void testPush() {
        Flux.push(sink -> {
                    for (int i = 1; i < 11; i++) {
                        sink.next(i);
                    }
                    sink.complete();
                }, FluxSink.OverflowStrategy.ERROR)
                .log()
                .subscribe(this::consoleConsumer);
    }

    /**
     * 创建一个Flux，该Flux从最近发出的Publisher镜像数据，直到新的Publisher进入源为止。
     * 生成的Flux将在没有新的Publisher进入源（源已完成）且最后一个镜像的Publisher也已完成时完成。
     *
     * <p>
     * `Flux.switchOnNext` 方法通常用于处理嵌套的流。它将发出数据的流中的每个数据都转换为另一个流，并合并这些流，从而生成一个新的单一的流。
     * <p>
     * 使用场景：
     * 1. 当需要从输入流中接收多个数据流时，可以使用 `switchOnNext` 将这些嵌套流合并成一个流。这对于需要根据输入动态切换不同数据流的场景非常有用。
     * 2. 如果需要使用一系列不同的策略来操作数据流并希望根据特定的策略动态地选择不同的流， `switchOnNext` 可以帮助选择合适的策略，并将其与数据流集成。
     * 3. 在处理多个数据源时，它还可以将它们组合为单个流以便于处理。例如，在静态数据源和动态量测数据源之间切换。
     * <p>
     * 需要注意的是，由于 switchOnNext() 方法将流平铺成单一的流，它可能会消耗大量内存，因此需要根据场景精心设计。
     * 此外，由于它是一个操作符，因此前置流（即其上游）必须是 `Flux` 或 `Mono` 类型。
     * </p>
     */
    @Test
    public void testSwitchOnNext() throws InterruptedException {
        /**
         * 假设我们有一个输入流，该流会发射其他流，而这些流本身也会发射数据。我们希望将这些内部流中的数据合并到单个流中以便进一步处理。在这种情况下，就可以使用 `switchOnNext` 来处理。
         *
         * 为了演示这一点，假设我们有一系列 ID，每个 ID 对应着一个不同的随机数流。我们可以使用 `Flux.interval` 来模拟这些随机数流。下面是代码：
         *
         * ```java
         * import reactor.core.publisher.Flux;
         * import java.time.Duration;
         * import java.util.concurrent.ThreadLocalRandom;
         *
         * public class SwitchOnNextExample {
         *
         *     public static void main(String[] args) throws InterruptedException {
         *
         *         Flux<String> ids = Flux.just("id1", "id2", "id3");
         *
         *         Flux<Flux<Integer>> randomNumbers = ids
         *                 .map(id -> Flux.interval(Duration.ofSeconds(1))
         *                         .map(tick -> ThreadLocalRandom.current().nextInt(100))
         *                         .take(5)
         *                 );
         *
         *         Flux.switchOnNext(randomNumbers)
         *                 .subscribe(num -> System.out.println("Random number: " + num));
         *
         *         Thread.sleep(20000);
         *
         *     }
         * }
         * ```
         *
         * 上面的代码中，我们首先创建了一个字符串流来模拟 ID 流。接下来，我们使用 `map` 操作符来对每个 ID 创建一个随机数流，并将这些随机数流放入另一个流中。
         * 我们使用 `interval` 操作符来模拟随机数发射的时间间隔，并使用 `take` 操作符来控制随机数流的长度为 5。
         *
         * 最后，我们使用 `switchOnNext` 操作符来将内部随机数流中的数据合并到单个流中，并订阅该流以输出生成的随机数。在这里，我们选择休眠 20 秒钟以确保能够捕获足够数量的随机数。
         *
         * 需要注意的是，由于 `switchOnNext` 操作符将多个流合并为一个流，因此需要小心使用。假设我们有几个包含大量数据的流，那么将这些流合并为单个流可能会导致内存问题。
         */
        Flux<String> ids = Flux.just("id1", "id2", "id3");

        Flux<Flux<Integer>> randomNumbers = ids
                .map(id -> Flux.interval(Duration.ofSeconds(1))
                        .map(tick -> ThreadLocalRandom.current().nextInt(100))
                        .take(5)
                );

        Flux.switchOnNext(randomNumbers)
                .subscribe(num -> System.out.println("Random number: " + num));

        Thread.sleep(20000);
    }

    /**
     * 在从同一资源派生的发布者流式传输值的同时，为每个单独的订阅者生成的Publisher使用资源，并在序列终止或订阅者取消时确保释放资源。
     * 急切的资源清理发生在源终止之前，清理消费者引发的异常可能会覆盖终端事件。
     */
    @Test
    public void testUsing() {
        Flux.using(() -> Files.newBufferedReader(Path.of(new ClassPathResource("text.txt").getFile().getAbsolutePath())),
                        br -> {
                            try {
                                return Mono.just(br.readLine());
                            } catch (IOException e) {
                                return Mono.error(e);
                            }
                        },
                        br -> {
                            try {
                                br.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        })
                .log()
                .subscribe(this::consoleConsumer);
    }

    @Test
    public void testUsingWhen() {

    }

    /**
     * 将两个数据源一起压缩，即等待所有的源都发出一个元素，然后将这些元素组合成一个 `Tuple2`。
     * 该运算符将继续这样做，直到任何一个源完成。错误将立即被转发。这种“逐步合并”处理在分散-聚合场景中特别有用。
     */
    @Test
    public void testZip() {
        Flux<Integer> flux1 = Flux.just(1, 2, 3, 4, 5);
        Flux<String> flux2 = Flux.just("A", "B", "C", "D", "E");

        Flux.zip(flux1, flux2)
                .log()
                .subscribe(this::consoleConsumer);
    }

    public <T> void consoleConsumer(T value) {
        System.out.println(value);
    }

}
