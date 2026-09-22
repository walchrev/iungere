package me.steinborn.krypton.mod.shared.network.util;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;

/**
 * Minecraft VarInt helpers that only depend on Netty, so the handlers don't need any
 * Minecraft classes (and don't care what they are called in the current mappings).
 */
public final class VarInts {
    private VarInts() {
    }

    public static int read(ByteBuf buf) {
        int value = 0;
        int shift = 0;
        byte b;
        do {
            b = buf.readByte();
            value |= (b & 0x7F) << shift;
            shift += 7;
            if (shift > 35) {
                throw new DecoderException("VarInt too big");
            }
        } while ((b & 0x80) == 0x80);
        return value;
    }

    public static void write(ByteBuf buf, int value) {
        while ((value & ~0x7F) != 0) {
            buf.writeByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        buf.writeByte(value);
    }
}
