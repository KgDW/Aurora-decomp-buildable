package kassuk.addon.aurora.modules;

import kassuk.addon.aurora.BlackOut;
import kassuk.addon.aurora.BlackOutModule;
import kassuk.addon.aurora.enums.RotationType;
import kassuk.addon.aurora.managers.Managers;
import kassuk.addon.aurora.utils.BOBlockUtil;
import kassuk.addon.aurora.utils.PlaceData;
import kassuk.addon.aurora.utils.SettingUtils;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Objects;

public class AntiPiston extends BlackOutModule
{
    int slot;

    public AntiPiston() {
        super(BlackOut.BLACKOUT, "AntiPiston", "1");
        this.slot = -1;
    }

    @EventHandler
    public void onTick(final TickEvent.Post event) {
        if (nullCheck()) {
            return;
        }
        if (!this.mc.player.isOnGround()) {
            return;
        }
        this.slot = InvUtils.findInHotbar(Items.OBSIDIAN).slot();
        if (this.slot == -1) {
            return;
        }
        final BlockPos eyePos = BlockPos.ofFloored(this.mc.player.getEyePos());
        if (!BOBlockUtil.isAir(eyePos.up())) {
            return;
        }
        for (final Direction direction : Direction.values()) {
            if (direction != Direction.DOWN && direction != Direction.UP) {
                if (BOBlockUtil.getBlock(eyePos.offset(direction)) instanceof PistonBlock || BOBlockUtil.getBlock(eyePos.offset(direction)) == Blocks.MOVING_PISTON || BOBlockUtil.getBlock(eyePos.offset(direction)) == Blocks.PISTON_HEAD) {
                    this.doPlace(Hand.MAIN_HAND, eyePos.offset(direction.getOpposite()));
                    this.doPlace(Hand.MAIN_HAND, eyePos.offset(direction).up());
                }
            }
        }
    }

    public void doPlace(final Hand hand, final BlockPos pos) {
        if (!BOBlockUtil.isAir(pos) || BOBlockUtil.cantBlockPlace(pos)) {
            return;
        }
        final PlaceData data = SettingUtils.getPlaceData(pos);
        if (!data.valid()) {
            return;
        }
        if (SettingUtils.shouldRotate(RotationType.BlockPlace) && !Managers.ROTATION.start(data.pos(), this.priority, RotationType.BlockPlace, Objects.hash(this.name + "placing"))) {
            return;
        }
        InvUtils.swap(this.slot, true);
        this.placeBlock(hand, data.pos().toCenterPos(), data.dir(), data.pos());
        InvUtils.swapBack();
        if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
            Managers.ROTATION.end(Objects.hash(this.name + "placing"));
        }
    }
}
