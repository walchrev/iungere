package me.steinborn.krypton.mod.shared.network.compression;

import com.velocitypowered.natives.compression.VelocityCompressor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.MessageToMessageDecoder;
import me.steinborn.krypton.mod.shared.network.util.VarInts;

import java.util.List;

import static com.velocitypowered.natives.util.MoreByteBufUtils.ensureCompatible;
import static com.velocitypowered.natives.util.MoreByteBufUtils.preferredBuffer;

/**
 * Decompresses a Minecraft 1.8.x packet. Behaves like vanilla's decoder: same limits, and packets
 * marked "uncompressed" (size 0) are passed through without extra checks, so odd proxies and
 * servers keep working.
 */
public class MinecraftCompressDecoder extends MessageToMessageDecoder<ByteBuf> {
    // Vanilla 1.8.9's limit
    private static final int MAXIMUM_UNCOMPRESSED_SIZE = 2097152;

    private int threshold;
    private final VelocityCompressor compressor;

    public MinecraftCompressDecoder(int threshold, VelocityCompressor compressor) {
        this.threshold = threshold;
        this.compressor = compressor;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int claimedUncompressedSize = VarInts.read(in);
        if (claimedUncompressedSize == 0) {
            out.add(in.retain());
            return;
        }

        if (claimedUncompressedSize < threshold) {
            throw new DecoderException("Badly compressed packet - size of " + claimedUncompressedSize
                    + " is below server threshold of " + threshold);
        }
        if (claimedUncompressedSize > MAXIMUM_UNCOMPRESSED_SIZE) {
            throw new DecoderException("Badly compressed packet - size of " + claimedUncompressedSize
                    + " is larger than protocol maximum of " + MAXIMUM_UNCOMPRESSED_SIZE);
        }

        ByteBuf compatibleIn = ensureCompatible(ctx.alloc(), compressor, in);
        ByteBuf uncompressed = preferredBuffer(ctx.alloc(), compressor, claimedUncompressedSize);
        try {
            compressor.inflate(compatibleIn, uncompressed, claimedUncompressedSize);
            out.add(uncompressed);
        } catch (Exception e) {
            uncompressed.release();
            throw e;
        } finally {
            compatibleIn.release();
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        compressor.close();
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
