package com.robining.netty;

public enum CmdType {
    HEART(0),
    MESSAGE(1);

    private int value;

    CmdType(int value) {
        this.value = value;
    }

    public static CmdType typeOf(int value) {
        for (CmdType type : values()) {
            if (type.value == value) {
                return type;
            }
        }

        return null;
    }

    public int getValue() {
        return value;
    }
}
