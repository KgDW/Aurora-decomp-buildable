package kassuk.addon.aurora.globalsettings;

import kassuk.addon.aurora.BlackOut;
import kassuk.addon.aurora.BlackOutModule;
import kassuk.addon.aurora.utils.OLEPOSSUtils;
import kassuk.addon.aurora.utils.PlaceData;
import kassuk.addon.aurora.utils.SettingUtils;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.function.Predicate;

/**
 * @author OLEPOSSU
 */

public class FacingSettings extends BlackOutModule {
    public FacingSettings() {
        super(BlackOut.SETTINGS, "Facing", "Global facing settings for every blackout module.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    //  Place Ranges
    public final Setting<Boolean> strictDir = sgGeneral.add(new BoolSetting.Builder()
        .name("Strict Direction")
        .description("Doesn't place on faces which aren't in your direction.")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> unblocked = sgGeneral.add(new BoolSetting.Builder()
        .name("Unblocked")
        .description("Doesn't place on faces that have block on them.")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> airPlace = sgGeneral.add(new BoolSetting.Builder()
        .name("Air Place")
        .description("Allows placing blocks in air.")
        .defaultValue(false)
        .build()
    );
    public final Setting<MaxHeight> maxHeight = sgGeneral.add(new EnumSetting.Builder<MaxHeight>()
        .name("Max Height")
        .description("Doesn't place on top sides of blocks at max height. Old: 1.12, New: 1.17+")
        .defaultValue(MaxHeight.New)
        .build()
    );


    public PlaceData getPlaceDataOR(BlockPos pos, Predicate<BlockPos> predicate, boolean ignoreContainers) {
        if (pos == null) {
            return new PlaceData(null, null, false);
        }
        Direction best = null;
        if (mc.world != null && mc.player != null) {
            if (airPlace.get()) {
                return new PlaceData(pos, Direction.UP, true);
            } else {
                double cDist = -1;
                for (Direction dir : Direction.values()) {

                    if (heightCheck(pos.offset(dir))) {
                        continue;
                    }

                    if (ignoreContainers && mc.world.getBlockState(pos.offset(dir)).hasBlockEntity()) {
                        continue;
                    }

                    if (!OLEPOSSUtils.solid(pos.offset(dir)) && (predicate != null && !predicate.test(pos.offset(dir)))) {
                        continue;
                    }

                    if (strictDir.get() && !OLEPOSSUtils.strictDir(pos.offset(dir), dir.getOpposite())) {
                        continue;
                    }

                    double dist = SettingUtils.placeRangeTo(pos.offset(dir));
                    if (dist >= 0 && (cDist < 0 || dist < cDist)) {
                        best = dir;
                        cDist = dist;
                    }
                }
            }
        }
        return best == null ? new PlaceData(null, null, false) : new PlaceData(pos.offset(best), best.getOpposite(), true);
    }

    public PlaceData getPlaceDataAND(BlockPos pos, Predicate<Direction> predicate, Predicate<BlockPos> predicatePos, boolean ignoreContainers) {
        if (pos == null) {
            return new PlaceData(null, null, false);
        }
        Direction best = null;
        if (mc.world != null && mc.player != null) {
            if (airPlace.get()) {
                return new PlaceData(pos, Direction.UP, true);
            } else {
                double cDist = -1;
                for (Direction dir : Direction.values()) {

                    if (heightCheck(pos.offset(dir))) {
                        continue;
                    }

                    if (ignoreContainers && mc.world.getBlockState(pos.offset(dir)).hasBlockEntity()) {
                        continue;
                    }

                    if (!OLEPOSSUtils.solid(pos.offset(dir)) || (predicate != null && !predicate.test(dir)) || (predicatePos != null && !predicatePos.test(pos.offset(dir)))) {
                        continue;
                    }

                    if (strictDir.get() && !OLEPOSSUtils.strictDir(pos.offset(dir), dir.getOpposite())) {
                        continue;
                    }

                    double dist = SettingUtils.placeRangeTo(pos.offset(dir));
                    if (dist >= 0 && (cDist < 0 || dist < cDist)) {
                        best = dir;
                        cDist = dist;
                    }
                }
            }
        }
        return best == null ? new PlaceData(null, null, false) : new PlaceData(pos.offset(best), best.getOpposite(), true);
    }

    public PlaceData getPlaceData(BlockPos pos, boolean ignoreContainers) {
        if (pos == null) {
            return new PlaceData(null, null, false);
        }
        Direction best = null;
        if (mc.world != null && mc.player != null) {
            if (airPlace.get()) {
                return new PlaceData(pos, Direction.UP, true);
            } else {
                double cDist = -1;
                for (Direction dir : Direction.values()) {

                    if (heightCheck(pos.offset(dir))) {
                        continue;
                    }

                    if (ignoreContainers && mc.world.getBlockState(pos.offset(dir)).hasBlockEntity() ) {
                        continue;
                    }

                    if (!OLEPOSSUtils.solid(pos.offset(dir))) {
                        continue;
                    }

                    if (strictDir.get() && !OLEPOSSUtils.strictDir(pos.offset(dir), dir.getOpposite())) {
                        continue;
                    }

                    double dist = SettingUtils.placeRangeTo(pos.offset(dir));
                    if (dist >= 0 && (cDist < 0 || dist < cDist)) {
                        best = dir;
                        cDist = dist;
                    }
                }
            }
        }
        return best == null ? new PlaceData(null, null, false) : new PlaceData(pos.offset(best), best.getOpposite(), true);
    }

    public Direction getPlaceOnDirection(BlockPos pos) {
        if (pos == null) {
            return null;
        }
        Direction best = null;
        if (mc.world != null && mc.player != null) {
            double cDist = -1;
            for (Direction dir : Direction.values()) {

                if (heightCheck(pos.offset(dir))) {
                    continue;
                }// Unblocked check (mostly for autocrystal placement facings)
                if (unblocked.get() && !(getBlock(pos.offset(dir)) instanceof AirBlock)) {
                    continue;
                }

                if (strictDir.get() && !OLEPOSSUtils.strictDir(pos, dir)) {
                    continue;
                }

                double dist = dist(pos, dir);
                if (dist >= 0 && (cDist < 0 || dist < cDist)) {
                    best = dir;
                    cDist = dist;
                }
            }
        }
        return best;
    }

    private boolean heightCheck(BlockPos pos) {
        return pos.getY() >= switch (maxHeight.get()) {
            case Old -> 255;
            case New -> 319;
            case Disabled -> 1000;
        };
    }

    private double dist(BlockPos pos, Direction dir) {
        if (mc.player == null) {
            return 0;
        }

        Vec3d vec = new Vec3d(pos.getX() + dir.getOffsetX() / 2f, pos.getY() + dir.getOffsetY() / 2f, pos.getZ() + dir.getOffsetZ() / 2f);
        Vec3d dist = mc.player.getEyePos().add(-vec.x, -vec.y, -vec.z);

        return Math.sqrt(dist.x * dist.x + dist.y * dist.y + dist.z * dist.z);
    }

    private Block getBlock(BlockPos pos) {
        if (mc.world != null) {
            return mc.world.getBlockState(pos).getBlock();
        }
        return null;
    }

    public enum MaxHeight {
        Old,
        New,
        Disabled
    }
}
