package com.robining.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class RobinPacketDecoder extends ByteToMessageDecoder {
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < RobinPacket.HEADER_BYTES) {
            return;
        }
        System.out.println(">>>try decoded packet");
        byteBuf.markReaderIndex();
        CmdType cmdType = CmdType.typeOf(byteBuf.readInt());
        int length = byteBuf.readInt();
        if (byteBuf.readableBytes() < length) {
            byteBuf.resetReaderIndex();
            System.out.println(">>>found packet,but data is not completed,wait...");
            return;
        }

        byte[] data = new byte[length];
        byteBuf.readBytes(data);
        RobinPacket packet = new RobinPacket().setCmdType(cmdType).setData(new String(data, StandardCharsets.UTF_8));
        list.add(packet);
        System.out.println(">>>decoded packet:" + packet.getCmdType());
    }
}
