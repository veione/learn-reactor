package com.think.reactor.netty.tcp.discard;

import io.netty.handler.ssl.util.SelfSignedCertificate;
import reactor.core.publisher.Flux;
import reactor.netty.tcp.TcpServer;
import reactor.netty.tcp.TcpSslContextSpec;

/**
 * A TCP server that discards the received data.
 * @author veione
 * @version 1.0.0
 * @date 2023年06月26日 10:23:00
 */
public class DiscardServer {
    static final boolean SECURE = System.getProperty("secure") != null;
    static final int PORT = Integer.parseInt(System.getProperty("port", SECURE ? "8443" : "8080"));
    static final boolean WIRETAP = System.getProperty("wiretap") != null;

    public static void main(String[] args) throws Exception {
        TcpServer server =
                TcpServer.create()
                        .port(PORT)
                        .wiretap(WIRETAP)
                        .handle((in, out) -> {
                            // Discards the incoming data and release the buffers
                            Flux<String> flux = in.receive().asString();
                            flux.doOnNext(System.out::println).subscribe();
                            return out.neverComplete();
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
