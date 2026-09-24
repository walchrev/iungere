package me.walch.iungere.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class MixinClientPlayerEntity {

    @Shadow
    public float pitch;

    @Shadow
    public float yaw;

    @Overwrite
    public Vec3d getLookVector() {
        float pitch = this.pitch;
        float yaw = this.yaw;

        float yawRad = yaw * 0.017453292F;
        float pitchRad = pitch * 0.017453292F;

        double x = -Math.sin(yawRad) * Math.cos(pitchRad);
        double y = -Math.sin(pitchRad);
        double z = Math.cos(yawRad) * Math.cos(pitchRad);

        return new Vec3d(x, y, z);
    }
}
