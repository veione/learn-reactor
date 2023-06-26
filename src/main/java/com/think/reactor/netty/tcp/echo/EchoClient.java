package com.think.reactor.netty.tcp.echo;

import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.Connection;
import reactor.netty.tcp.TcpClient;
import reactor.netty.tcp.TcpSslContextSpec;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;

/**
 * A TCP client that sends a package to the TCP server and
 * later echoes the received content thus initiating ping/pong.
 *
 * @author veione
 * @version 1.0.0
 * @date 2023年06月26日 09:21:00
 */
public final class EchoClient {

    static final boolean SECURE = System.getProperty("secure") != null;
    static final int PORT = Integer.parseInt(System.getProperty("port", SECURE ? "8443" : "8080"));
    static final boolean WIRETAP = System.getProperty("wiretap") != null;

    public static void main(String[] args) {
        TcpClient client =
                TcpClient.create()
                        .port(PORT)
                        .wiretap(WIRETAP)
                        .doOnConnected(connection -> connection.addHandlerLast(new DelimiterBasedFrameDecoder(8092, Delimiters.lineDelimiter())));

        if (SECURE) {
            TcpSslContextSpec ssc =
                    TcpSslContextSpec.forClient()
                            .configure(builder -> builder.trustManager(InsecureTrustManagerFactory.INSTANCE));
            client = client.secure(spec -> spec.sslContext(ssc));
        }

        Connection connection =
                client.handle((in, out) -> {
                            in
                                    .receive()
                                    .asString(StandardCharsets.UTF_8)
                                    .doOnNext(System.out::println)
                                    .subscribe();
                            return out.sendString(Flux.interval(Duration.ofSeconds(3)).map(l -> "echo " + l + "\r\n"));
                        })
                        .connectNow();

        connection.onDispose()
                .block();
    }
}
