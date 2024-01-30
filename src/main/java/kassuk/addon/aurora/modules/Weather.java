package kassuk.addon.aurora.modules;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.Tessellator;
import net.minecraft.util.math.MathHelper;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import kassuk.addon.aurora.BlackOut;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import kassuk.addon.aurora.BlackOutModule;

public class Weather extends BlackOutModule
{
    private final SettingGroup sgGeneral;
    private final Setting<Boolean> snow;
    private final Setting<Integer> Height;
    private final Setting<Double> strength;

    public Weather() {
        super(BlackOut.BLACKOUT, "Weather", "1");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.snow = (Setting<Boolean>)this.sgGeneral.add((Setting) new BoolSetting.Builder().name("Snow").description(" ").defaultValue(true).build());
        this.Height = (Setting<Integer>)this.sgGeneral.add((Setting) new IntSetting.Builder().name("Height").min(0).max(255).defaultValue(80).build());
        this.strength = (Setting<Double>)this.sgGeneral.add((Setting) new DoubleSetting.Builder().name("Strength").min(0.1).max(2.0).defaultValue(0.8).build());
    }

    @EventHandler
    public void OnRender(final Render3DEvent event) {
        final double f = this.strength.get();
        if (!Utils.canUpdate()) {
            return;
        }
        final Entity entity = this.mc.getCameraEntity();
        if (entity == null) {
            return;
        }
        final World world = this.mc.world;
        final int i = MathHelper.floor((float)entity.getBlockX());
        final int j = MathHelper.floor((float)entity.getBlockY());
        final int k = MathHelper.floor((float)entity.getBlockZ());
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager._disableCull();
    }
}
