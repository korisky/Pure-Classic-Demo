package com.http3.demo;

import com.http3.demo.handler.Http3ServerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.incubator.codec.http3.Http3;
import io.netty.incubator.codec.http3.Http3ServerConnectionHandler;
import io.netty.incubator.codec.quic.*;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author Roylic
 * 2023/1/30
 */
public class Http3Server {


    private Http3Server() {
    }

    public static void main(String[] args) throws Exception {

        // for ssl context building
        SelfSignedCertificate cert = new SelfSignedCertificate();
        QuicSslContext sslContext = QuicSslContextBuilder
                .forServer(cert.key(), null, cert.cert())
                .applicationProtocols(Http3.supportedApplicationProtocols())
                .build();

        // for channel handler initialization
        ChannelHandler codec = Http3.newQuicServerCodecBuilder()
                .sslContext(sslContext)
                .maxIdleTimeout(5000, TimeUnit.MINUTES)
                .initialMaxData(10000000)
                .initialMaxStreamDataBidirectionalLocal(1000000)
                .initialMaxStreamDataBidirectionalRemote(1000000)
                .initialMaxStreamsBidirectional(100)
                .tokenHandler(InsecureQuicTokenHandler.INSTANCE)
                .handler(new ChannelInitializer<QuicChannel>() {
                    @Override
                    protected void initChannel(QuicChannel ch) throws Exception {
                        // called for each connection
                        ch.pipeline().addLast(
                                new Http3ServerConnectionHandler(
                                        new ChannelInitializer<QuicStreamChannel>() {
                                            @Override
                                            protected void initChannel(QuicStreamChannel ch) throws Exception {
                                                // called for each request-stream
                                                ch.pipeline().addLast(new Http3ServerHandler());
                                            }
                                        }));
                    }
                }).build();

        // start netty http3 server
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            Channel channel = bootstrap.group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(codec)
                    .bind(new InetSocketAddress(9999))
                    .sync()
                    .channel();
            channel.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
