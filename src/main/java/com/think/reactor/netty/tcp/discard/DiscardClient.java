package com.think.reactor.netty.tcp.discard;

import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.core.publisher.Flux;
import reactor.netty.Connection;
import reactor.netty.tcp.TcpClient;
import reactor.netty.tcp.TcpSslContextSpec;

import java.time.Duration;

/**
 * A TCP client that sends random data to the TCP server and
 * discard the received data.
 *
 * @author veione
 * @version 1.0.0
 * @date 2023年06月26日 10:23:00
 */
public final class DiscardClient {
    static final boolean SECURE = System.getProperty("secure") != null;
    static final int PORT = Integer.parseInt(System.getProperty("port", SECURE ? "8443" : "8080"));
    static final boolean WIRETAP = System.getProperty("wiretap") != null;

    public static void main(String[] args) {
        TcpClient client =
                TcpClient.create()
                        .port(PORT)
                        .wiretap(WIRETAP);

        if (SECURE) {
            TcpSslContextSpec tcpSslContextSpec =
                    TcpSslContextSpec.forClient()
                            .configure(builder -> builder.trustManager(InsecureTrustManagerFactory.INSTANCE));
            client = client.secure(spec -> spec.sslContext(tcpSslContextSpec));
        }

        Connection connection =
                client.handle((in, out) -> {
                    // Discards the incoming data and releases the buffers;
                    in.receive().asString().doOnNext(System.out::println).subscribe();
                    return out.sendString(Flux.interval(Duration.ofMillis(100)).map(l -> l + ""));
                }).connectNow();

        connection.onDispose()
                .retry()
                .block();
    }
}
