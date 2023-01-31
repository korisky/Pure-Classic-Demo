package com.http3.demo;

import com.http3.demo.handler.Http3ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.incubator.codec.http3.DefaultHttp3HeadersFrame;
import io.netty.incubator.codec.http3.Http3;
import io.netty.incubator.codec.http3.Http3ClientConnectionHandler;
import io.netty.incubator.codec.quic.QuicChannel;
import io.netty.incubator.codec.quic.QuicSslContext;
import io.netty.incubator.codec.quic.QuicSslContextBuilder;
import io.netty.incubator.codec.quic.QuicStreamChannel;
import io.netty.util.NetUtil;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author Roylic
 * 2023/1/31
 */
public class Http3Client {

    private Http3Client() {
    }

    public static void main(String[] args) throws Exception {

        // ssl context
        QuicSslContext sslContext = QuicSslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .applicationProtocols(Http3.supportedApplicationProtocols())
                .build();

        // for client, do not need to take care of ->
        // 1) maxStreamDataBidirectionalRemote -> only server can take care of remote
        // 2) tokenHandler / channel initialization handler -> only server init for channels
        ChannelHandler codec = Http3.newQuicClientCodecBuilder()
                .sslContext(sslContext)
                .maxIdleTimeout(5000, TimeUnit.MINUTES)
                .initialMaxData(10000000)
                .initialMaxStreamDataBidirectionalLocal(1000000)
                .build();

        NioEventLoopGroup group = new NioEventLoopGroup(1);
        try {
            Bootstrap bs = new Bootstrap();
            Channel channel = bs.group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(codec)
                    .bind(0)
                    .sync()
                    .channel();

            QuicChannel quicChannel = QuicChannel.newBootstrap(channel)
                    .handler(new Http3ClientConnectionHandler())
                    .remoteAddress(new InetSocketAddress(NetUtil.LOCALHOST4, 9999))
                    .connect()
                    .get();

            QuicStreamChannel streamChannel = Http3.newRequestStream(quicChannel, new Http3ClientHandler()).sync().getNow();

            // write the header frame and send the FIN to mark the end of the request
            // after this it is not possible anymore to write any more data
            DefaultHttp3HeadersFrame frame = new DefaultHttp3HeadersFrame();
            frame.headers().method("GET").path("/");
            streamChannel.writeAndFlush(frame).addListener(QuicStreamChannel.SHUTDOWN_OUTPUT).sync();

            // wait for the stream channel and quic channel to be closed, after this is done, will close the underlying datagram channel
            streamChannel.closeFuture().sync();

            // after received the response lets also close the underlying QUIC channel and datagram channel
            quicChannel.close().sync();
            channel.close().sync();

        } finally {
            group.shutdownGracefully();
        }


    }
}
