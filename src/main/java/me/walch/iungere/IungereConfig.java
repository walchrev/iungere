package me.walch.iungere;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Plain config POJO, persisted to config/iungere.json.
 *
 * Deliberately has zero dependency on Argentum/Celeritas so it loads fine
 * whether or not Argentum is installed. {@link me.walch.iungere.integration.ArgentumIntegration}
 * is the only place that reaches into Celeritas' option API, and it's only
 * ever touched when Argentum is actually present.
 */
public final class IungereConfig {
    private static final Logger LOGGER = LogManager.getLogger("Iungere");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("iungere.json");

    private static IungereConfig instance;

    public boolean hitDelayFix = true;
    public boolean mouseDelayFix = true;

    public static synchronized IungereConfig get() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    private static IungereConfig load() {
        if (Files.exists(PATH)) {
            try (Reader reader = Files.newBufferedReader(PATH, StandardCharsets.UTF_8)) {
                IungereConfig loaded = GSON.fromJson(reader, IungereConfig.class);
                if (loaded != null) {
                    return loaded;
                }
            } catch (IOException e) {
                LOGGER.warn("Could not read {}, falling back to defaults", PATH, e);
            }
        }

        IungereConfig defaults = new IungereConfig();
        defaults.save();
        return defaults;
    }

    public void save() {
        try {
            Path dir = PATH.getParent();
            if (dir != null && !Files.exists(dir)) {
                Files.createDirectories(dir);
            }

            Path tmp = PATH.resolveSibling(PATH.getFileName() + ".tmp");
            try (Writer writer = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
                GSON.toJson(this, writer);
            }
            Files.move(tmp, PATH, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            LOGGER.warn("Could not save {}", PATH, e);
        }
    }
}
