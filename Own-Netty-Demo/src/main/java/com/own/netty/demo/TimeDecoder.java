package com.own.netty.demo;

import com.own.netty.demo.pojo.UnixTime;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Decoder for handling fragment issue cause by stream-base msg model
 */
public class TimeDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < 4) {
            // because for time-server, we use 4 bytes allocated as one complete msg,
            // if we received < 4, msg must be fragmented, do nothing but waite for the whole msg
            return;
        }
        // add complete msg into the list, for further consumption
//        list.add(byteBuf.readBytes(4));
        list.add(new UnixTime(byteBuf.readUnsignedInt()));
    }
}
