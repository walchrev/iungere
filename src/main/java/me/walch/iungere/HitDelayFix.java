package me.walch.iungere;

import net.fabricmc.api.ModInitializer;
import me.walch.iungere.integration.ArgentumIntegration;
import net.fabricmc.loader.api.FabricLoader;

public class HitDelayFix implements ModInitializer {
	@Override
	public void onInitialize() {

		System.out.println("[HitDelayFix] Initalized");

		if (FabricLoader.getInstance().isModLoaded("argentum")) {
			ArgentumIntegration.register();
		}
	}
}
