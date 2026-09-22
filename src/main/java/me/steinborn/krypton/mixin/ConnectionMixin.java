package me.steinborn.krypton.mixin;

import com.velocitypowered.natives.compression.VelocityCompressor;
import com.velocitypowered.natives.encryption.VelocityCipher;
import com.velocitypowered.natives.util.Natives;
import io.netty.channel.Channel;
import me.steinborn.krypton.mod.shared.network.compression.MinecraftCompressDecoder;
import me.steinborn.krypton.mod.shared.network.compression.MinecraftCompressEncoder;
import me.steinborn.krypton.mod.shared.network.pipeline.MinecraftCipherDecoder;
import me.steinborn.krypton.mod.shared.network.pipeline.MinecraftCipherEncoder;
import net.minecraft.network.CompressionDecoder;
import net.minecraft.network.CompressionEncoder;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;

@Mixin(Connection.class)
public abstract class ConnectionMixin {
    @Shadow
    private Channel channel;
    @Shadow
    private boolean encrypted;

    @Inject(method = "setCompressionThreshold", at = @At("HEAD"), cancellable = true)
    private void krypton$setCompressionThreshold(int threshold, CallbackInfo ci) {
        Object decompress = this.channel.pipeline().get("decompress");
        Object compress = this.channel.pipeline().get("compress");

        if (threshold < 0) {
            if (decompress instanceof CompressionDecoder || decompress instanceof MinecraftCompressDecoder) {
                this.channel.pipeline().remove("decompress");
            }
            if (compress instanceof CompressionEncoder || compress instanceof MinecraftCompressEncoder) {
                this.channel.pipeline().remove("compress");
            }
        } else if (decompress instanceof MinecraftCompressDecoder && compress instanceof MinecraftCompressEncoder) {
            ((MinecraftCompressDecoder) decompress).setThreshold(threshold);
            ((MinecraftCompressEncoder) compress).setThreshold(threshold);
        } else {
            VelocityCompressor compressor = Natives.compress.get().create(4);
            this.channel.pipeline().addBefore("decoder", "decompress",
                    new MinecraftCompressDecoder(threshold, compressor));
            this.channel.pipeline().addBefore("encoder", "compress",
                    new MinecraftCompressEncoder(threshold, compressor));
        }

        ci.cancel();
    }

    // Feather leaves Connection.enableEncryption(SecretKey) unnamed; m_93804007 is its Ornithe gen2 intermediary name.
    @Inject(method = "m_93804007", at = @At("HEAD"), cancellable = true)
    private void krypton$enableEncryption(SecretKey key, CallbackInfo ci) throws GeneralSecurityException {
        this.encrypted = true;

        VelocityCipher decryption = Natives.cipher.get().forDecryption(key);
        VelocityCipher encryption = Natives.cipher.get().forEncryption(key);

        this.channel.pipeline().addBefore("splitter", "decrypt", new MinecraftCipherDecoder(decryption));
        this.channel.pipeline().addBefore("prepender", "encrypt", new MinecraftCipherEncoder(encryption));

        ci.cancel();
    }
}
