package com.own.netty.demo.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.StandardCharsets;

@Deprecated
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {


//    // Printing version of server
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) {
//        // discard the received data
//        // byteBuf is a referenced-counted obj, which has to be released explicitly via release()
//        // it is the handler's responsibility to release any reference-counted obj passed to the handler
//        // usually channelRead is like:
//        // @Override
//        // public void channelRead(ChannelHandlerContext ctx, Object msg) {
//        //    try {
//        //        // Do something with msg
//        //    } finally {
//        //        ReferenceCountUtil.release(msg);
//        //    }
//        //}
//
//        ByteBuf in = (ByteBuf) msg;
//        try {
//            while (in.isReadable()) {
//                // just print what we read in char
//                System.out.print((char) in.readByte());
//                System.out.flush();
//            }
//        } finally {
//            ReferenceCountUtil.release(msg);
//            // or type in.release
//        }
//    }

    // Echo version of server
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // normal step for casting obj 2 ByteBuf for further use
        ByteBuf in = (ByteBuf) msg;
        byte[] req = new byte[in.readableBytes()];
        in.readBytes(req);
        String msgStr = new String(req, StandardCharsets.UTF_8);
        System.out.println("Server received: " + msgStr);
        // construct response, here msg already been used, ctx.write(msg) would response nothing
        ByteBuf resp = Unpooled.copiedBuffer(msgStr.getBytes(StandardCharsets.UTF_8));
        // rather than release the ctx, Netty would help us release it after written it out to the wire
        ctx.write(resp);
        // write does not make the msg written out to the wire. It is buffered internally and then flushed out
        // by using .flush(). Or else, we could use .writeAndFlush(msg) for brevity
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // close the connection when an exception is raised
        cause.printStackTrace();
        ctx.close();
    }
}
