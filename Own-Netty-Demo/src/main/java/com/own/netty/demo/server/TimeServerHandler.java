package com.own.netty.demo.server;

import com.own.netty.demo.pojo.UnixTime;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * For sending msg once as soon as a connection is established
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // for writing a new msg, allocate a new buffer which will contain it -> 32bit -> 4bytes
//        final ByteBuf time = ctx.alloc().buffer(4);
//        time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));
//
//        // writeAndFlush is an async method, after it finished, have to close the channel
//        final ChannelFuture channelFuture = ctx.writeAndFlush(time);
//        channelFuture.addListener(
//                // or can just -> .addListener(ChannelFutureListener.CLOSE)
//                (ChannelFutureListener) future -> {
//                    assert channelFuture == future;
//                    ctx.close();
//                });

        ChannelFuture f = ctx.writeAndFlush(new UnixTime());
        f.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
