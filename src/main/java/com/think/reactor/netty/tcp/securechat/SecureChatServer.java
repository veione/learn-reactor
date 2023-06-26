package com.think.reactor.netty.tcp.securechat;

import io.netty.channel.ChannelId;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.tcp.TcpServer;
import reactor.netty.tcp.TcpSslContextSpec;

import javax.net.ssl.SSLSession;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月26日 10:46:00
 */
public class SecureChatServer {
    private static final int PORT = Integer.parseInt(System.getProperty("port", "8992"));
    private static final boolean WIRETAP = System.getProperty("wiretap") != null;

    public static void main(String[] args) throws Exception {
        ConcurrentHashMap<ChannelId, Connection> conns = new ConcurrentHashMap<>();
        String hostName = InetAddress.getLocalHost().getHostName();

        //SelfSignedCertificate ssc = new SelfSignedCertificate();
        TcpServer.create()
                .port(PORT)
                .wiretap(WIRETAP)
                .doOnConnection(connection -> {
                    // Cache the new connection. It'll be needed later
                    // when the server broadcasts messages from other clients.
                    ChannelId id = connection.channel().id();
                    conns.put(id, connection);
                    connection.addHandlerLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                    connection.onDispose(() -> conns.remove(id));
                })
                .handle((in, out) -> {
                    List<String> welcomeTexts = new ArrayList<>();
                    welcomeTexts.add("Welcome to " + hostName + " secure chat service!\n");
                    in.withConnection(connection -> {
                        SslHandler handler = connection.channel().pipeline().get(SslHandler.class);
                        if (handler != null) {
                            SSLSession session = handler.engine().getSession();
                            String cipherSuite = session.getCipherSuite();
                            String msg = "Your session is protected by " + cipherSuite + " cipher suite.\n";
                            welcomeTexts.add(msg);
                        }
                    });

                    Flux<String> welcomeFlux = Flux.fromIterable(welcomeTexts);

                    Flux<String> flux =
                            in.receive()
                                    .asString()
                                    .takeUntil("bye"::equalsIgnoreCase)
                                    .handle((text, sink) -> in.withConnection(current -> {
                                        for (Connection conn : conns.values()) {
                                            if (conn == current) {
                                                sink.next(text);
                                            }
                                            else {
                                                String msg = "[" + conn.channel().remoteAddress() + "] " + text + '\n';
                                                conn.outbound().sendString(Mono.just(msg)).then().subscribe();
                                            }
                                        }
                                    }))
                                    .map(msg -> "[you] " + msg + '\n');
                    return out.sendString(Flux.concat(welcomeFlux, flux));
                })
                //.secure(spec -> spec.sslContext(TcpSslContextSpec.forServer(ssc.certificate(), ssc.privateKey())))
                .bindNow()
                .onDispose()
                .block();
    }
}
