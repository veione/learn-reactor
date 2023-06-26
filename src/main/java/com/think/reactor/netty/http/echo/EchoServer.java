package com.think.reactor.netty.http.echo;

import io.netty.handler.ssl.util.SelfSignedCertificate;
import reactor.netty.http.Http11SslContextSpec;
import reactor.netty.http.Http2SslContextSpec;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.server.HttpServer;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.TEXT_PLAIN;

/**
 * An HTTP server that expects POST request and sends back the content of the received HTTP request.
 *
 * @author veione
 * @version 1.0.0
 * @date 2023年06月26日 11:28:00
 */
public class EchoServer {
    static final boolean SECURE = System.getProperty("secure") != null;
    static final int PORT = Integer.parseInt(System.getProperty("port", SECURE ? "8443" : "8080"));
    static final boolean WIRETAP = System.getProperty("wiretap") != null;
    static final boolean COMPRESS = System.getProperty("compress") != null;
    static final boolean HTTP2 = System.getProperty("http2") != null;

    public static void main(String[] args) throws Exception {
        HttpServer server =
                HttpServer.create()
                        .accessLog(true)
                        .port(PORT)
                        .wiretap(WIRETAP)
                        .compress(COMPRESS)
                        .route(r -> r.post("/echo",
                                (req, res) -> res.header(CONTENT_TYPE, TEXT_PLAIN)
                                        .send(req.receive().retain())));

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
