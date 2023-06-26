package com.think.reactor.netty.http.echo;

import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.http.Http11SslContextSpec;
import reactor.netty.http.client.HttpClient;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月26日 11:53:00
 */
public class EchoClient {
    static final boolean SECURE = System.getProperty("secure") != null;
    static final int PORT = Integer.parseInt(System.getProperty("port", SECURE ? "8443" : "8080"));
    static final boolean WIRETAP = System.getProperty("wiretap") != null;
    static final boolean COMPRESS = System.getProperty("compress") != null;

    public static void main(String[] args) {
        HttpClient client =
                HttpClient.create()
                        .port(PORT)
                        .wiretap(WIRETAP)
                        .compress(COMPRESS);

        if (SECURE) {
            Http11SslContextSpec http11SslContextSpec =
                    Http11SslContextSpec.forClient()
                            .configure(builder -> builder.trustManager(InsecureTrustManagerFactory.INSTANCE));
            client = client.secure(spec -> spec.sslContext(http11SslContextSpec));
        }

        String response =
                client.post()
                        .uri("/echo")
                        .send(ByteBufFlux.fromString(Mono.just("echo")))
                        .responseContent()
                        .aggregate()
                        .asString()
                        .block();

        System.out.println("Response: " + response);
    }
}
