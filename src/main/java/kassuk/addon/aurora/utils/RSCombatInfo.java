package kassuk.addon.aurora.utils;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos;

public class RSCombatInfo
{
    public static Box getCrystalBox(final BlockPos pos) {
        return new Box(pos.getX() - 0.5, pos.getY(), pos.getZ() - 0.5, pos.getX() + 1.5, pos.getY() + 2, pos.getZ() + 1.5);
    }

    public static Box getCrystalBox(final Vec3d pos) {
        return new Box(pos.getX() - 1.0, pos.getY(), pos.getZ() - 1.0, pos.getX() + 1.0, pos.getY() + 2.0, pos.getZ() + 1.0);
    }
}
