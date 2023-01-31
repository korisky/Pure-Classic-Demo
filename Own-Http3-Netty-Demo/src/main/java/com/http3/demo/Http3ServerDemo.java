package com.http3.demo;

import com.http3.demo.handler.Http3ServerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.incubator.codec.http3.*;
import io.netty.incubator.codec.quic.*;

import java.net.InetSocketAddress;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

public class Http3ServerDemo {


    public static final int PORT = 9999;


    private Http3ServerDemo() {
    }


    public static void main(String... args) throws Exception {

        // construct ssl context
        QuicSslContext sslContext = constructQUICServerSslCtx();

        // construct channel -> encryption & decryption & configuration
        ChannelHandler codec = Http3.newQuicServerCodecBuilder()
                .sslContext(sslContext)
                // idle means waiting not close connection (channel) even no traffic
                .maxIdleTimeout(5, TimeUnit.MINUTES)
                // When set to a non-zero value quiche will only allow at most v bytes of incoming stream data to
                // be buffered for the whole connection (that is, data that is not yet read by the application)
                // 10000000 = 10 Mb
                .initialMaxData(10000000)
                // only allow at most v bytes of incoming stream data for locally initiate bidirectional connection, 1 Mb
                .initialMaxStreamDataBidirectionalLocal(1000000)
                // only allow at most v bytes of incoming stream data for remotely initiate bidirectional connection, 1 Mb
                .initialMaxStreamDataBidirectionalRemote(1000000)
                // When set to a non-zero value quiche will only allow v number of concurrent remotely-initiated
                // bidirectional streams to be open at any given time and will increase the limit automatically as streams are completed.
                .initialMaxStreamsBidirectional(100)
                .tokenHandler(InsecureQuicTokenHandler.INSTANCE)
                // handler for QUIC-HTTP3, similar to the childHandler setting for normal Netty-Server
                .handler(new ChannelInitializer<QuicChannel>() {
                    @Override
                    protected void initChannel(QuicChannel ch) {
                        // Called for each connection
                        ch.pipeline().addLast(new Http3ServerConnectionHandler(
                                new ChannelInitializer<QuicStreamChannel>() {
                                    // Called for each request-stream
                                    @Override
                                    protected void initChannel(QuicStreamChannel ch) {
                                        ch.pipeline().addLast(new Http3ServerHandler());
                                    }
                                }));
                    }
                }).build();

        // start Netty for QUIC-Http3-Server
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        try {
            Bootstrap bs = new Bootstrap();
            Channel channel = bs.group(group)
                    // for QUIC using NioDatagramChannel rather than NioServerSocketChannel
                    // server's channel = client's channel
                    .channel(NioDatagramChannel.class)
                    .handler(codec)
                    .bind(new InetSocketAddress(PORT)).sync().channel();

            System.out.println("NETTY-HTTP3-QUIC Server start");
            channel.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    /**
     * building QUIC-Server used ssl context
     */
    private static QuicSslContext constructQUICServerSslCtx() throws CertificateException {
        SelfSignedCertificate cert = new SelfSignedCertificate();
        return QuicSslContextBuilder.forServer(cert.key(), null, cert.cert())
                .applicationProtocols(Http3.supportedApplicationProtocols()).build();
    }
}
