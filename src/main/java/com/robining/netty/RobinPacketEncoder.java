package com.robining.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

public class RobinPacketEncoder extends MessageToByteEncoder<RobinPacket> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RobinPacket value, ByteBuf byteBuf) throws Exception {
        System.out.println(">>>encoded packet start:" + value.getCmdType());
        byteBuf.writeInt(value.getCmdType().getValue());
        byteBuf.writeInt(value.getDataLength());
        if(value.getData() != null) {
            byteBuf.writeBytes(value.getData().getBytes(StandardCharsets.UTF_8));
        }
        System.out.println(">>>encoded packet end:" + value.getCmdType());
    }
}
