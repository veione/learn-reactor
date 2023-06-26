package com.think.reactor.netty.udp.qotm;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.DatagramPacket;
import reactor.core.publisher.Flux;
import reactor.netty.udp.UdpServer;

import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月26日 13:51:00
 */
public class QuoteOfTheMomentServer {
    private static final int PORT = Integer.parseInt(System.getProperty("port", "7686"));
    private static final boolean WIRETAP = System.getProperty("wiretap") != null;
    private static final Random random = new Random();

    private static final String[] quotes = {"Where there is love there is life.",
            "First they ignore you, then they laugh at you, then they fight you, then you win.",
            "Be the change you want to see in the world.",
            "The weak can never forgive. Forgiveness is the attribute of the strong."};

    private static String nextQuote() {
        int quoteId;
        synchronized (random) {
            quoteId = random.nextInt(quotes.length);
        }
        return quotes[quoteId];
    }

    public static void main(String[] args) {
        UdpServer server =
                UdpServer.create()
                        .port(PORT)
                        .wiretap(WIRETAP)
                        .option(ChannelOption.SO_BROADCAST, true)
                        .handle((in, out) -> {
                            Flux<DatagramPacket> inFlux =
                                    in.receiveObject()
                                            .handle((incoming, sink) -> {
                                                if (incoming instanceof DatagramPacket packet) {
                                                    String content = packet.content().toString(StandardCharsets.UTF_8);

                                                    if ("QOTM?".equalsIgnoreCase(content)) {
                                                        String nextQuote = nextQuote();
                                                        ByteBuf byteBuf =
                                                                Unpooled.copiedBuffer("QOTM: " + nextQuote, StandardCharsets.UTF_8);
                                                        DatagramPacket response = new DatagramPacket(byteBuf, packet.sender());
                                                        sink.next(response);
                                                    }
                                                }
                                            });
                            return out.sendObject(inFlux);
                        });

        server.bindNow()
                .onDispose()
                .block();
    }

}
