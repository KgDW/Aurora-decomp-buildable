package kassuk.addon.aurora.utils;

import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;

public class MathUtil
{
    public static Vec3d interpolateEntity(final Entity entity, final float time) {
        return new Vec3d(entity.lastRenderX + (entity.getX() - entity.lastRenderX) * time, entity.lastRenderY + (entity.getY() - entity.lastRenderY) * time, entity.lastRenderZ + (entity.getZ() - entity.lastRenderZ) * time);
    }
}
