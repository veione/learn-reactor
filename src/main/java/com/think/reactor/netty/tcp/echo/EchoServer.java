package com.think.reactor.netty.tcp.echo;

import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.tcp.TcpServer;
import reactor.netty.tcp.TcpSslContextSpec;

import java.util.Arrays;
import java.util.Date;

/**
 * A TCP server that sends back the received content.
 *
 * @author veione
 * @version 1.0.0
 * @date 2023年06月26日 09:15:00
 */
public class EchoServer {
    static final boolean SECURE = System.getProperty("secure") != null;
    static final int PORT = Integer.parseInt(System.getProperty("port", SECURE ? "8443" : "8080"));
    static final boolean WIRETAP = System.getProperty("wiretap") != null;

    public static void main(String[] args) throws Exception {
        TcpServer server =
                TcpServer.create()
                        .port(PORT)
                        .wiretap(WIRETAP)
                        .doOnConnection(connection -> {
                            System.out.println("客户端连接上来啦 = " + connection.toString());
                            connection.addHandlerLast(new DelimiterBasedFrameDecoder(8092, Delimiters.lineDelimiter()));
                        })
                        .handle((in, out) -> {
                            in.receive()
                                    .asString()
                                    .doOnNext(System.out::println)
                                    .subscribe();
                            Flux<String> welcomeFlux =
                                    Flux.just("Welcome to chat room!\r\n, It is " + new Date() + " now.\r\n");
                            Flux<String> responses =
                                    in.receive()
                                            .asString();
                            System.out.println("收到了客户端的消息啦");
                            return out.sendString(Flux.concat(welcomeFlux, responses));
                        });
        if (SECURE) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            server = server.secure(
                    spec -> spec.sslContext(TcpSslContextSpec.forServer(ssc.certificate(), ssc.privateKey())));
        }

        server.bindNow()
                .onDispose()
                .block();
    }
}
