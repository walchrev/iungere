package me.walch.iungere.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class HitDelayFixMixin {

    @Shadow
    private int attackCooldown;

    @Inject(method = "doAttack", at = @At("HEAD"))
    private void resetAttackCooldown(CallbackInfo ci) {
        this.attackCooldown = 0;
    }
}
