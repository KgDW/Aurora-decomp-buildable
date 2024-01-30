package kassuk.addon.aurora.utils;

import net.minecraft.util.math.BlockPos;
import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.block.AirBlock;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;

public class CrystalUtil
{
    public static Vec3d getMotionVec(final Entity entity, final int ticks, final boolean collision) {
        final double dX = entity.getX() - entity.prevX;
        final double dZ = entity.getZ() - entity.prevZ;
        double entityMotionPosX = 0.0;
        double entityMotionPosZ = 0.0;
        if (collision) {
            if (MeteorClient.mc.world != null) {
                for (int i = 1; i <= ticks && MeteorClient.mc.world.getBlockState(new BlockPos((int)(entity.getBlockX() + dX * i), entity.getBlockX(), (int)(entity.getBlockZ() + dZ * i))).getBlock() instanceof AirBlock; ++i) {
                    entityMotionPosX = dX * i;
                    entityMotionPosZ = dZ * i;
                }
            }
        }
        else {
            entityMotionPosX = dX * ticks;
            entityMotionPosZ = dZ * ticks;
        }
        return new Vec3d(entityMotionPosX, 0.0, entityMotionPosZ);
    }
}
