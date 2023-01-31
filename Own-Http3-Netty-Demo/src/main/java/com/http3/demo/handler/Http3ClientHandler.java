package com.http3.demo.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.incubator.codec.http3.Http3DataFrame;
import io.netty.incubator.codec.http3.Http3HeadersFrame;
import io.netty.incubator.codec.http3.Http3RequestStreamFrame;
import io.netty.incubator.codec.http3.Http3RequestStreamInboundHandler;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.StandardCharsets;

/**
 * @author Roylic
 * 2023/1/31
 */
public class Http3ClientHandler extends Http3RequestStreamInboundHandler {

    @Override
    protected void channelRead(ChannelHandlerContext ctx, Http3HeadersFrame frame, boolean isLast) throws Exception {
        releaseFrameAndCloseIfLast(ctx, frame, isLast);
    }

    @Override
    protected void channelRead(ChannelHandlerContext ctx, Http3DataFrame frame, boolean isLast) throws Exception {
        System.out.println(frame.content().toString(StandardCharsets.UTF_8));
        releaseFrameAndCloseIfLast(ctx, frame, isLast);
    }

    private void releaseFrameAndCloseIfLast(ChannelHandlerContext ctx, Http3RequestStreamFrame frame, boolean isLast) {
        ReferenceCountUtil.release(frame);
        if (isLast) {
            ctx.close();
        }
    }
}
