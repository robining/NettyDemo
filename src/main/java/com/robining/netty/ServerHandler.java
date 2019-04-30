package com.robining.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.StandardCharsets;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private int readHeartLossCount = 0;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println(">>>Server init completed!!");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println(">>>Server closed!!");
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);

        RobinPacket robinPacket = (RobinPacket) msg;
        System.out.println(">>>cmdType:" + robinPacket.getCmdType());
        System.out.println(">>>data:" + robinPacket.getData());
        ReferenceCountUtil.release(msg);

        if(robinPacket.getCmdType() == CmdType.HEART){
            return;
        }

        new Thread() {
            @Override
            public void run() {
                RobinPacket reply = new RobinPacket().setCmdType(CmdType.MESSAGE).setData("Hello Client,I'm Server Reply!!");
                ByteBuf byteBuf = ctx.alloc().buffer(Integer.SIZE / 8);
                byteBuf.writeInt(reply.getCmdType().getValue());
                ctx.writeAndFlush(byteBuf);
                System.out.println(">>>已回复CmdType");

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                byteBuf = ctx.alloc().buffer(Integer.SIZE / 8);
                byteBuf.writeInt(reply.getDataLength());
                ctx.writeAndFlush(byteBuf);
                System.out.println(">>>已回复长度");

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                byteBuf = ctx.alloc().buffer(reply.getDataLength());
                byteBuf.writeBytes(reply.getData().getBytes(StandardCharsets.UTF_8));
                ctx.writeAndFlush(byteBuf);
                System.out.println(">>>已回复所有内容");

            }
        }.start();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            if (((IdleStateEvent) evt).state() == IdleState.READER_IDLE) {
                if (evt == IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT) {
                    readHeartLossCount = 1;
                } else if (evt == IdleStateEvent.READER_IDLE_STATE_EVENT) {
                    readHeartLossCount++;
                }

                System.out.println("客户端心跳丢失次数:" + readHeartLossCount);
                if (readHeartLossCount >= 3) {
                    System.out.println("客户端心跳包丢失3次以上,断开连接");
                    ctx.channel().closeFuture().sync();
                }
            }
        }
    }
}
