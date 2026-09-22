package me.steinborn.krypton.mod.shared;

import com.velocitypowered.natives.util.Natives;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KryptonSharedInitializer implements ModInitializer {
    private static final Logger LOGGER = LogManager.getLogger(KryptonSharedInitializer.class);

    static {
        // Netty's default 16MiB arenas are far more than Minecraft needs (2MiB max packet size).
        // maxOrder 9 gives 4MiB chunks. Respect an explicit user setting.
        if (System.getProperty("io.netty.allocator.maxOrder") == null) {
            System.setProperty("io.netty.allocator.maxOrder", "9");
        }
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Krypton: compression will use {}, encryption will use {}",
                Natives.compress.getLoadedVariant(), Natives.cipher.getLoadedVariant());
    }
}
