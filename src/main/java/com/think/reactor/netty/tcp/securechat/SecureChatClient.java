package com.think.reactor.netty.tcp.securechat;

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
 * @date 2023年06月26日 10:46:00
 */
public class SecureChatClient {
    private static final String HOST = System.getProperty("host", "127.0.0.1");
    private static final int PORT = Integer.parseInt(System.getProperty("port", "8992"));
    private static final boolean WIRETAP = System.getProperty("wiretap") != null;

    public static void main(String[] args) {
        TcpSslContextSpec tcpSslContextSpec =
                TcpSslContextSpec.forClient()
                        .configure(builder -> builder.trustManager(InsecureTrustManagerFactory.INSTANCE));

        TcpClient client =
                TcpClient.create()
                        .host(HOST)
                        .port(PORT)
                        .wiretap(WIRETAP)
                        .doOnConnected(connection ->
                                connection.addHandlerLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter())));
                        //.secure(spec -> spec.sslContext(tcpSslContextSpec));

        Connection connection = client.connectNow();

        connection.inbound()
                .receive()
                .asString()
                .subscribe(System.out::println);

        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8.name());
        while (scanner.hasNext()) {
            String text = scanner.nextLine();
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
