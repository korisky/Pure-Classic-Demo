package com.http3.demo.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.incubator.codec.http3.*;
import io.netty.incubator.codec.quic.QuicStreamChannel;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.StandardCharsets;

/**
 * For handling Http3 request
 *
 * @author Roylic
 * 2023/1/31
 */
public class Http3ServerHandler extends Http3RequestStreamInboundHandler {


    private static final byte[] CONTENT = "Hello World!\r\n".getBytes(StandardCharsets.UTF_8);


    @Override
    protected void channelRead(ChannelHandlerContext ctx, Http3HeadersFrame frame, boolean isLast) throws Exception {
        if (isLast) {
            writeResponse(ctx);
        }
        ReferenceCountUtil.release(frame);
    }

    @Override
    protected void channelRead(ChannelHandlerContext ctx, Http3DataFrame frame, boolean isLast) throws Exception {
        if (isLast) {
            writeResponse(ctx);
        }
        ReferenceCountUtil.release(frame);
    }

    private void writeResponse(ChannelHandlerContext ctx) {
        DefaultHttp3HeadersFrame headersFrame = new DefaultHttp3HeadersFrame();
        headersFrame.headers().status("404");
        headersFrame.headers().add("server", "netty");
        headersFrame.headers().addInt("content-length", CONTENT.length);
        ctx.write(headersFrame);
        ctx.writeAndFlush(
                        new DefaultHttp3DataFrame(
                                Unpooled.wrappedBuffer(CONTENT)))
                // adding SHUTDOWN_OUTPUT = WRITE_FIN => data transfer finished signal
                .addListener(QuicStreamChannel.SHUTDOWN_OUTPUT);
    }
}
