package kassuk.addon.aurora.utils;

import kassuk.addon.aurora.utils.Util;
import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class BOBlockUtil {
    public static boolean solid2(BlockPos block) {
        return MeteorClient.mc.world.getBlockState(block).isSolid();
    }

    public static boolean isAir(BlockPos block) {
        return MeteorClient.mc.world.getBlockState(block).isAir() || BOBlockUtil.getBlock(block) == Blocks.FIRE;
    }

    public static Block getBlock(BlockPos block) {
        return MeteorClient.mc.world.getBlockState(block).getBlock();
    }

    public static boolean isAir(Vec3d vec3d) {
        return Util.mc.world.getBlockState(BOBlockUtil.vec3toBlockPos(vec3d)).getBlock().equals(Blocks.AIR);
    }

    public static BlockPos vec3toBlockPos(Vec3d vec3d) {
        return new BlockPos((int)Math.floor(vec3d.x), (int)Math.round(vec3d.y), (int)Math.floor(vec3d.z));
    }

    public static double getPushDistance(PlayerEntity player, double x, double z) {
        double d0 = player.getX() - x;
        double d2 = player.getZ() - z;
        return Math.sqrt(d0 * d0 + d2 * d2);
    }

    public static BlockState getState(BlockPos pos) {
        return MeteorClient.mc.world.getBlockState(pos);
    }

    public static boolean fakeBBoxCheckFeet(PlayerEntity player, Vec3d offset) {
        Vec3d futurePos = player.getPos().add(offset);
        return BOBlockUtil.isAir(futurePos.add(0.3, 0.0, 0.3)) && BOBlockUtil.isAir(futurePos.add(-0.3, 0.0, 0.3)) && BOBlockUtil.isAir(futurePos.add(0.3, 0.0, -0.3)) && BOBlockUtil.isAir(futurePos.add(-0.3, 0.0, 0.0)) && BOBlockUtil.isAir(futurePos.add(0.0, 0.0, 0.3)) && BOBlockUtil.isAir(futurePos.add(0.3, 0.0, 0.0)) && BOBlockUtil.isAir(futurePos.add(0.0, 0.0, -0.3));
    }

    public static BlockPos getFlooredPosition(Entity entity) {
        return new BlockPos((int)Math.floor(entity.getX()), (int)Math.round(entity.getY()), (int)Math.floor(entity.getZ()));
    }

    public static boolean cantBlockPlace(BlockPos blockPos) {
        if (MeteorClient.mc.world.getBlockState(blockPos.add(0, 0, 1)).getBlock() == Blocks.AIR && MeteorClient.mc.world.getBlockState(blockPos.add(0, 0, -1)).getBlock() == Blocks.AIR && MeteorClient.mc.world.getBlockState(blockPos.add(1, 0, 0)).getBlock() == Blocks.AIR && MeteorClient.mc.world.getBlockState(blockPos.add(-1, 0, 0)).getBlock() == Blocks.AIR && MeteorClient.mc.world.getBlockState(blockPos.add(0, 1, 0)).getBlock() == Blocks.AIR && MeteorClient.mc.world.getBlockState(blockPos.add(0, -1, 0)).getBlock() == Blocks.AIR) {
            return true;
        }
        return !MeteorClient.mc.world.getBlockState(blockPos).isAir() && BOBlockUtil.getBlock(blockPos) != Blocks.FIRE;
    }
}

