package com.think.reactor.netty.udp.qotm;

import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.udp.UdpClient;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月26日 13:51:00
 */
public class QuoteOfTheMomentClient {
    private static final int PORT = Integer.parseInt(System.getProperty("port", "7686"));
    private static final boolean WIRETAP = System.getProperty("wiretap") != null;

    public static void main(String[] args) {
        UdpClient client =
                UdpClient.create()
                        .port(PORT)
                        .wiretap(WIRETAP);

        Connection conn = client.connectNow();

        conn.inbound()
                .receive()
                .asString()
                .doOnNext(text -> {
                    if (text.startsWith("QOTM:")) {
                        System.out.println("Quote of the Moment: " + text.substring(6));
                        conn.disposeNow();
                    }
                })
                .doOnError(err -> {
                    err.printStackTrace();
                    conn.disposeNow();
                })
                .subscribe();

        conn.outbound()
                .sendString(Mono.just("QOTM?"))
                .then()
                .subscribe();

        conn.onReadIdle(5000, () -> {
            System.err.println("QOTM request time out.");
            conn.disposeNow();
        });

        conn.onDispose()
                .block();
    }
}
