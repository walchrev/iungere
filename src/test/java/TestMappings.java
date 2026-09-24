import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class TestMappings {
    void test(LocalClientPlayerEntity player) {
        player.getLookVector();
        player.pitch = 0.0F;
        player.yaw = 0.0F;

        Vec3d v = player.getLookVector();
    }
}
