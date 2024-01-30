package kassuk.addon.aurora.modules;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.block.BlockState;
import net.minecraft.util.hit.HitResult;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.world.BlockView;
import net.minecraft.util.hit.BlockHitResult;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import kassuk.addon.aurora.BlackOut;
import kassuk.addon.aurora.utils.FadeUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import kassuk.addon.aurora.BlackOutModule;

public class BlockSelectionPlus extends BlackOutModule
{
    private final SettingGroup sgGeneral;
    private final Setting<ShapeMode> shapeMode;
    private final Setting<SettingColor> sideColor;
    private final Setting<SettingColor> lineColor;
    private final Setting<Boolean> hideInside;
    private final Setting<Integer> animationTime;
    Box renderBB;
    BlockPos lastPos;
    FadeUtils fade;

    public BlockSelectionPlus() {
        super(BlackOut.BLACKOUT, "block-selection-plus", "Modifies how your block selection is rendered.");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.shapeMode = (Setting<ShapeMode>)this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)new EnumSetting.Builder().name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
        this.sideColor = (Setting<SettingColor>)this.sgGeneral.add((Setting) new ColorSetting.Builder().name("side-color").description("The side color.").defaultValue(new SettingColor(255, 255, 255, 50)).build());
        this.lineColor = (Setting<SettingColor>)this.sgGeneral.add((Setting) new ColorSetting.Builder().name("line-color").description("The line color.").defaultValue(new SettingColor(255, 255, 255, 255)).build());
        this.hideInside = (Setting<Boolean>)this.sgGeneral.add((Setting) new BoolSetting.Builder().name("hide-when-inside-block").description("Hide selection when inside target block.").defaultValue(true).build());
        this.animationTime = (Setting<Integer>)this.sgGeneral.add((Setting) new IntSetting.Builder().name("Animation Move Speed").description("How fast should aurora mode box move.").defaultValue(1).min(0).sliderRange(0, 10).build());
        this.fade = new FadeUtils(this.animationTime.get());
    }

    @EventHandler
    private void onRender(final Render3DEvent event) {
        if (this.mc.crosshairTarget != null) {
            final HitResult crosshairTarget = this.mc.crosshairTarget;
            if (crosshairTarget instanceof final BlockHitResult result) {
                final BlockPos bp = result.getBlockPos();
                final BlockState state = this.mc.world.getBlockState(bp);
                final VoxelShape shape = state.getOutlineShape(this.mc.world, bp);
                final Box box = shape.getBoundingBox();
                if (this.lastPos == null) {
                    this.lastPos = bp;
                    this.renderBB = box;
                }
                if (!this.lastPos.equals(result.getBlockPos())) {
                    this.lastPos = result.getBlockPos();
                    this.fade = new FadeUtils(this.animationTime.get());
                }
                final BlockPos bp2 = result.getBlockPos();
                final BlockState state2 = this.mc.world.getBlockState(bp2);
                final VoxelShape shape2 = state2.getOutlineShape(this.mc.world, bp2);
                final Box bb2 = shape2.getBoundingBox();
                this.renderBB = new Box(this.renderBB.minX - (this.renderBB.minX - bb2.minX) * this.fade.easeOutQuad(), this.renderBB.minY - (this.renderBB.minY - bb2.minY) * this.fade.easeOutQuad(), this.renderBB.minZ - (this.renderBB.minZ - bb2.minZ) * this.fade.easeOutQuad(), this.renderBB.maxX - (this.renderBB.maxX - bb2.maxX) * this.fade.easeOutQuad(), this.renderBB.maxY - (this.renderBB.maxY - bb2.maxY) * this.fade.easeOutQuad(), this.renderBB.maxZ - (this.renderBB.maxZ - bb2.maxZ) * this.fade.easeOutQuad());
                event.renderer.box(this.renderBB, this.sideColor.get(), this.lineColor.get(), this.shapeMode.get(), 0);
            }
        }
    }

    private void render(final Render3DEvent event, final Box renderBB) {
        event.renderer.box(renderBB, this.sideColor.get(), this.lineColor.get(), this.shapeMode.get(), 0);
    }
}
