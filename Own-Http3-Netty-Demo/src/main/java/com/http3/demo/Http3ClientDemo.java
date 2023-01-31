package com.http3.demo;

import com.http3.demo.handler.Http3ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.incubator.codec.http3.*;
import io.netty.incubator.codec.quic.QuicChannel;
import io.netty.incubator.codec.quic.QuicSslContext;
import io.netty.incubator.codec.quic.QuicSslContextBuilder;
import io.netty.incubator.codec.quic.QuicStreamChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import io.netty.util.ReferenceCountUtil;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Http3ClientDemo {


    private Http3ClientDemo() {
    }


    public static void main(String... args) throws Exception {

        NioEventLoopGroup group = new NioEventLoopGroup(1);

        try {
            // for client, do not need to create new CERT
            QuicSslContext context = QuicSslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .applicationProtocols(Http3.supportedApplicationProtocols()).build();

            // construct channel -> encryption & decryption & configuration
            ChannelHandler codec = Http3.newQuicClientCodecBuilder()
                    .sslContext(context)
                    .maxIdleTimeout(5, TimeUnit.MINUTES)
                    .initialMaxData(10000000)
                    .initialMaxStreamDataBidirectionalLocal(1000000)
                    .build();

            // start Netty-Client server
            Bootstrap bs = new Bootstrap();
            Channel channel = bs.group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(codec)
                    .bind(9998).sync().channel();

            QuicChannel quicChannel = createChannelAndSendMsg(channel);

            // After we received the response lets also close the underlying QUIC channel and datagram channel.
            quicChannel.close().sync();
            channel.close().sync();

        } finally {
            group.shutdownGracefully();
        }
    }


    private static QuicChannel createChannelAndSendMsg(Channel channel) throws InterruptedException, ExecutionException {

        // create connection target
        QuicChannel quicChannel = QuicChannel.newBootstrap(channel)
                .handler(new Http3ClientConnectionHandler())
                .remoteAddress(new InetSocketAddress(NetUtil.LOCALHOST4, Http3ServerDemo.PORT))
                .connect()
                .get();

        // create stream for handling incoming msg
        QuicStreamChannel streamChannel = Http3.newRequestStream(quicChannel, new Http3ClientHandler())
                .sync().getNow();

        System.out.println("NETTY-HTTP3-QUIC Client start and sent msg");

        // Write the Header frame and send the FIN to mark the end of the request.
        // After this it is not possible anymore to write any more data.
        Http3HeadersFrame frame = new DefaultHttp3HeadersFrame();
        frame.headers().method("GET").path("/")
                .authority(NetUtil.LOCALHOST4.getHostAddress() + ":" + Http3ServerDemo.PORT)
                .scheme("https");
        streamChannel.writeAndFlush(frame)
                .addListener(QuicStreamChannel.SHUTDOWN_OUTPUT).sync();

        // Wait for the stream channel and quic channel to be closed (this will happen after we received the FIN).
        // After this is done we will close the underlying datagram channel.
        streamChannel.closeFuture().sync();
        return quicChannel;
    }
}
