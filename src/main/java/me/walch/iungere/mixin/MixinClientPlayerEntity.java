package me.walch.iungere.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import me.walch.iungere.IungereConfig;

@Mixin(Entity.class)
public abstract class MixinClientPlayerEntity {

    @Shadow
    public float pitch;

    @Shadow
    public float yaw;

    @Inject(method = "getLookVector", at = @At("HEAD"), cancellable = true)
    private void iungere$mouseDelayFix(CallbackInfoReturnable<Vec3d> cir) {
        if (!IungereConfig.get().mouseDelayFix) {
            return;
        }

        float pitch = this.pitch;
        float yaw = this.yaw;

        float yawRad = yaw * 0.017453292F;
        float pitchRad = pitch * 0.017453292F;

        double x = -Math.sin(yawRad) * Math.cos(pitchRad);
        double y = -Math.sin(pitchRad);
        double z = Math.cos(yawRad) * Math.cos(pitchRad);

        cir.setReturnValue(new Vec3d(x, y, z));
    }
}
