package me.steinborn.krypton.mod.shared.network.compression;

import com.velocitypowered.natives.compression.VelocityCompressor;
import com.velocitypowered.natives.util.MoreByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import me.steinborn.krypton.mod.shared.network.util.VarInts;

public class MinecraftCompressEncoder extends MessageToByteEncoder<ByteBuf> {
    private int threshold;
    private final VelocityCompressor compressor;

    public MinecraftCompressEncoder(int threshold, VelocityCompressor compressor) {
        this.threshold = threshold;
        this.compressor = compressor;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        int uncompressed = msg.readableBytes();
        if (uncompressed < threshold) {
            VarInts.write(out, 0);
            out.writeBytes(msg);
        } else {
            VarInts.write(out, uncompressed);
            ByteBuf compatibleIn = MoreByteBufUtils.ensureCompatible(ctx.alloc(), compressor, msg);
            try {
                compressor.deflate(compatibleIn, out);
            } finally {
                compatibleIn.release();
            }
        }
    }

    @Override
    protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, ByteBuf msg, boolean preferDirect) throws Exception {
        // Compressed data is at most the input size minus one for anything worth compressing, and
        // the uncompressed case is exactly one byte larger than the input.
        return MoreByteBufUtils.preferredBuffer(ctx.alloc(), compressor, msg.readableBytes() + 1);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        compressor.close();
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
