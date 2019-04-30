package com.robining.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

public class ClientHandler extends ChannelInboundHandlerAdapter{
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println(">>>Client init completed!!");
        ctx.writeAndFlush(RobinPacket.messagePacket("Hello! Server.I'm Client active"));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println(">>>Client closed!!");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        RobinPacket packet = (RobinPacket) msg;
        System.out.println(">>>cmdType:" + packet.getCmdType());
        System.out.println(">>>data:" + packet.getData());

        ReferenceCountUtil.release(msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);

        if (evt instanceof IdleStateEvent) {
            if (((IdleStateEvent) evt).state() == IdleState.WRITER_IDLE) {
                System.out.println(">>>客户端长时间未发送数据,发送心跳包");
                ctx.channel().writeAndFlush(RobinPacket.heartPackaet());
            }
        }
    }
}
