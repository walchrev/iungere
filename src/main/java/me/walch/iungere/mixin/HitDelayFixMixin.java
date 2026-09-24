package me.walch.iungere.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import me.walch.iungere.IungereConfig;

@Mixin(Minecraft.class)
public class HitDelayFixMixin {

    @Shadow
    private int attackCooldown;

    @Inject(method = "doAttack", at = @At("HEAD"))
    private void resetAttackCooldown(CallbackInfo ci) {
        if (!IungereConfig.get().hitDelayFix) {
            return;
        }

        this.attackCooldown = 0;
    }
}
