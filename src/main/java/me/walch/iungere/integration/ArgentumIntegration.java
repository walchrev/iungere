package me.walch.iungere.integration;

import me.walch.iungere.IungereConfig;
import org.embeddedt.embeddium.impl.gui.framework.TextComponent;
import org.taumc.celeritas.api.OptionGUIConstructionEvent;
import org.taumc.celeritas.api.options.OptionIdentifier;
import org.taumc.celeritas.api.options.control.TickBoxControl;
import org.taumc.celeritas.api.options.structure.OptionGroup;
import org.taumc.celeritas.api.options.structure.OptionImpl;
import org.taumc.celeritas.api.options.structure.OptionPage;
import org.taumc.celeritas.api.options.structure.OptionStorage;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Registers an Iungere page in Argentum's settings menu (via Celeritas'
 * OptionGUIConstructionEvent), mirroring the pattern Argentum's own
 * "extras" companion mod uses.
 *
 * IMPORTANT: only call {@link #register()} after confirming Argentum is
 * loaded (see HitDelayFix). Every type referenced in this class comes
 * from Celeritas, so this class must never be touched when Argentum
 * isn't present, or class loading will fail.
 */
public final class ArgentumIntegration {
    private static final String MOD_ID = "iungere";

    private static final OptionStorage<IungereConfig> CONFIG_STORAGE = new OptionStorage<>() {
        @Override
        public IungereConfig getData() {
            return IungereConfig.get();
        }

        @Override
        public void save() {
            IungereConfig.get().save();
        }
    };

    private ArgentumIntegration() {
    }

    public static void register() {
        OptionGUIConstructionEvent.BUS.addListener(event -> createPages().forEach(event::addPage));
    }

    private static List<OptionPage> createPages() {
        OptionGroup fixes = OptionGroup.createBuilder()
                .setId(id("fixes"))
                .add(toggle("hit_delay_fix", (c, v) -> c.hitDelayFix = v, c -> c.hitDelayFix))
                .add(toggle("mouse_delay_fix", (c, v) -> c.mouseDelayFix = v, c -> c.mouseDelayFix))
                .build();

        return List.of(new OptionPage(id("main"), text("pages.main"), List.of(fixes)));
    }

    private static OptionImpl<IungereConfig, Boolean> toggle(String name,
            BiConsumer<IungereConfig, Boolean> setter, Function<IungereConfig, Boolean> getter) {
        return OptionImpl.createBuilder(boolean.class, CONFIG_STORAGE)
                .setId(id(name))
                .setName(text(name))
                .setTooltip(text(name + ".tooltip"))
                .setControl(TickBoxControl::new)
                .setBinding(setter, getter)
                .build();
    }

    private static <T> OptionIdentifier<T> id(String path) {
        return OptionIdentifier.create(MOD_ID, path).cast();
    }

    private static TextComponent text(String path, Object... args) {
        return TextComponent.translatable(MOD_ID + ".options." + path, args);
    }
}
