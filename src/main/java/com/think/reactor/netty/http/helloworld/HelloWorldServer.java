package com.think.reactor.netty.http.helloworld;

import io.netty.handler.ssl.util.SelfSignedCertificate;
import reactor.core.publisher.Mono;
import reactor.netty.http.Http11SslContextSpec;
import reactor.netty.http.Http2SslContextSpec;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.server.HttpServer;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.TEXT_PLAIN;

/**
 * An HTTP server that expects GET request and sends back "Hello World!".
 *
 * @author veione
 * @version 1.0.0
 * @date 2023年06月26日 13:44:00
 */
public class HelloWorldServer {
    static final boolean SECURE = System.getProperty("secure") != null;
    static final int PORT = Integer.parseInt(System.getProperty("port", SECURE ? "8443" : "8080"));
    static final boolean WIRETAP = System.getProperty("wiretap") != null;
    static final boolean COMPRESS = System.getProperty("compress") != null;
    static final boolean HTTP2 = System.getProperty("http2") != null;

    public static void main(String[] args) throws Exception {
        HttpServer server =
                HttpServer.create()
                        .port(PORT)
                        .wiretap(WIRETAP)
                        .compress(COMPRESS)
                        .route(r -> r.get("/hello",
                                (req, res) -> res.header(CONTENT_TYPE, TEXT_PLAIN)
                                        .sendString(Mono.just("Hello,World"))));
        if (SECURE) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            if (HTTP2) {
                server = server.secure(spec -> spec.sslContext(Http2SslContextSpec.forServer(ssc.certificate(), ssc.privateKey())));
            } else {
                server = server.secure(spec -> spec.sslContext(Http11SslContextSpec.forServer(ssc.certificate(), ssc.privateKey())));
            }
        }

        if (HTTP2) {
            server = server.protocol(HttpProtocol.H2);
        }

        server.bindNow()
                .onDispose()
                .block();
    }
}
