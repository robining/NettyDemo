package com.robining.netty;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class RobinPacket {
    public static final int HEADER_BYTES = (Integer.SIZE + Integer.SIZE) / 8;
    private static final RobinPacket HEART_PACKET = new RobinPacket().setCmdType(CmdType.HEART);
    private CmdType cmdType;
    private String data;

    public CmdType getCmdType() {
        return cmdType;
    }

    public RobinPacket setCmdType(CmdType cmdType) {
        this.cmdType = cmdType;
        return this;
    }

    public int getDataLength() {
        if (data == null || "".equals(data)) {
            return 0;
        }

        return data.getBytes(StandardCharsets.UTF_8).length;
    }

    public int getPacketLength() {
        return HEADER_BYTES + getDataLength();
    }

    public String getData() {
        return data;
    }

    public RobinPacket setData(String data) {
        this.data = data;
        return this;
    }

    public static RobinPacket heartPackaet() {
        return HEART_PACKET;
    }

    public static RobinPacket messagePacket(String data) {
        return new RobinPacket().setCmdType(CmdType.MESSAGE).setData(data);
    }
}
