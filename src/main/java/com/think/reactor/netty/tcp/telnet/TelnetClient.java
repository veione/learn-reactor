package com.think.reactor.netty.tcp.telnet;

import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.tcp.TcpClient;
import reactor.netty.tcp.TcpSslContextSpec;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月26日 09:49:00
 */
public class TelnetClient {

    static final boolean SECURE = System.getProperty("secure") != null;
    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", SECURE ? "8992" : "8993"));
    static final boolean WIRETAP = System.getProperty("wiretap") != null;

    public static void main(String... args) {
        TcpClient client =
                TcpClient.create()
                        .host(HOST)
                        .port(PORT)
                        .doOnConnected(connection ->
                                connection.addHandlerLast(new DelimiterBasedFrameDecoder(8092, Delimiters.lineDelimiter())))
                        .wiretap(WIRETAP);

        if (SECURE) {
            TcpSslContextSpec tcpSslContextSpec =
                    TcpSslContextSpec.forClient()
                            .configure(builder -> builder.trustManager(InsecureTrustManagerFactory.INSTANCE));
            client = client.secure(spec -> spec.sslContext(tcpSslContextSpec));
        }

        Connection connection = client.connectNow();

        connection.inbound()
                .receive()
                .asString(StandardCharsets.UTF_8)
                .doOnNext(System.out::println)
                .subscribe();

        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8.name());
        while (scanner.hasNext()) {
            String text = scanner.next();
            connection.outbound()
                    .sendString(Mono.just(text + "\r\n"))
                    .then()
                    .subscribe();
            if ("bye".equalsIgnoreCase(text)) {
                break;
            }
        }

        connection.onDispose()
                .block();
    }
}
