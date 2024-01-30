package kassuk.addon.aurora.modules;

import kassuk.addon.aurora.BlackOut;
import kassuk.addon.aurora.BlackOutModule;
import kassuk.addon.aurora.utils.meteor.BOEntityUtils;
import meteordevelopment.meteorclient.events.entity.LivingEntityMoveEvent;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.Anchor;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.Vec3d;

public class BurrowMove extends BlackOutModule
{
    private final SettingGroup sgGeneral;
    public final Setting<Double> speed;

    public BurrowMove() {
        super(BlackOut.BLACKOUT, "Burrow Move", "Allow you move in burrow.");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.speed = (Setting<Double>)this.sgGeneral.add((Setting) new DoubleSetting.Builder().name("Speed").description("The speed in blocks per second.").defaultValue(0.3).range(0.0, 1.0).sliderRange(0.0, 1.0).build());
    }

    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        if (BOEntityUtils.isBurrowed(this.mc.player)) {
            final Vec3d vel = PlayerUtils.getHorizontalVelocity(this.speed.get());
            double velX = vel.getX();
            double velZ = vel.getZ();
            if (this.mc.player.hasStatusEffect(StatusEffects.SPEED)) {
                final double value = (this.mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier() + 1) * 0.205;
                velX += velX * value;
                velZ += velZ * value;
            }
            final Anchor anchor = (Anchor)Modules.get().get((Class)Anchor.class);
            if (anchor.isActive() && anchor.controlMovement) {
                velX = anchor.deltaX;
                velZ = anchor.deltaZ;
            }
            ((IVec3d)event.movement).set(velX, event.movement.y, velZ);
        }
    }

    @EventHandler
    public void onLivingEntityMove(final LivingEntityMoveEvent event) {
        if (event.entity != this.mc.player) {
            return;
        }
        if (BOEntityUtils.isBurrowed(this.mc.player)) {
            final Vec3d vel = PlayerUtils.getHorizontalVelocity(this.speed.get());
            double velX = vel.getX();
            double velZ = vel.getZ();
            if (this.mc.player.hasStatusEffect(StatusEffects.SPEED)) {
                final double value = (this.mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier() + 1) * 0.205;
                velX += velX * value;
                velZ += velZ * value;
            }
            final Anchor anchor = (Anchor)Modules.get().get((Class)Anchor.class);
            if (anchor.isActive() && anchor.controlMovement) {
                velX = anchor.deltaX;
                velZ = anchor.deltaZ;
            }
            ((IVec3d)event.movement).set(velX, event.movement.y, velZ);
        }
    }
}
