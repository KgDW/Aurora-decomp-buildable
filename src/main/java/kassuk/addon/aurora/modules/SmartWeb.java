package kassuk.addon.aurora.modules;

import kassuk.addon.aurora.utils.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import net.minecraft.util.math.Box;
import kassuk.addon.aurora.utils.PlaceData;
import net.minecraft.util.Hand;
import kassuk.addon.aurora.managers.Managers;
import kassuk.addon.aurora.enums.RotationType;
import kassuk.addon.aurora.utils.SettingUtils;
import net.minecraft.entity.Entity;
import meteordevelopment.meteorclient.systems.friends.Friends;
import java.util.LinkedHashSet;
import java.util.function.Predicate;
import kassuk.addon.aurora.utils.BOBlockUtil;
import net.minecraft.block.Blocks;
import kassuk.addon.aurora.utils.Util;
import net.minecraft.util.math.BlockPos;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import java.util.Iterator;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.settings.ColorSetting;
import java.util.Objects;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import java.util.ArrayList;
import kassuk.addon.aurora.BlackOut;
import kassuk.addon.aurora.utils.Timer;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import net.minecraft.entity.player.PlayerEntity;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import java.util.List;
import kassuk.addon.aurora.BlackOutModule;

public class SmartWeb extends BlackOutModule
{
    private final List<Render> renderBlocks;
    private final SettingGroup sgGeneral;
    private final SettingGroup sgRender;
    private final Setting<Boolean> down;
    private final Setting<Boolean> feet;
    private final Setting<Boolean> face;
    private final Setting<Integer> surCheck;
    private final Setting<Double> minSpeed;
    private final Setting<Boolean> onlyGround;
    private final Setting<Boolean> pauseEat;
    private final Setting<Boolean> CheckMine;
    private final Setting<Boolean> CheckSelf;
    private final Setting<Boolean> CheckFriend;
    private final Setting<Double> range;
    private final Setting<Integer> multiPlace;
    private final Setting<Integer> delay;
    private List<PlayerEntity> targets;
    private final Setting<Boolean> render;
    private final Setting<Double> renderTime;
    private final Setting<Double> fadeTime;
    private final Setting<ShapeMode> shapeMode;
    private final Setting<SettingColor> lineColor;
    private final Setting<SettingColor> sideColor;
    private int progress;
    private final Timer timer;

    public SmartWeb() {
        super(BlackOut.BLACKOUT, "Smart Web", "Automatically places webs on other players.");
        this.renderBlocks = new ArrayList<Render>();
        this.sgGeneral = this.settings.getDefaultGroup();
        this.sgRender = this.settings.createGroup("Render");
        this.down = (Setting<Boolean>)this.sgGeneral.add((Setting) new BoolSetting.Builder().name("Down").description("1").defaultValue(true).build());
        this.feet = (Setting<Boolean>)this.sgGeneral.add((Setting) new BoolSetting.Builder().name("Feet").description("ji ao.").defaultValue(true).build());
        this.face = (Setting<Boolean>)this.sgGeneral.add((Setting) new BoolSetting.Builder().name("Face").description("tou.").defaultValue(true).build());
        this.surCheck = (Setting<Integer>)this.sgGeneral.add((Setting) new IntSetting.Builder().name("Surround Check").defaultValue(5).min(0).sliderRange(0, 5).build());
        this.minSpeed = (Setting<Double>)this.sgGeneral.add((Setting) new DoubleSetting.Builder().name("target min speed").description("ddd.").defaultValue(2.0).range(0.0, 5.0).sliderMax(5.0).build());
        this.onlyGround = (Setting<Boolean>)this.sgGeneral.add((Setting) new BoolSetting.Builder().name("Only Ground").description("Pauses when you are fffffff.").defaultValue(false).build());
        this.pauseEat = (Setting<Boolean>)this.sgGeneral.add((Setting) new BoolSetting.Builder().name("Pause Eat").description("Pauses when you are eating.").defaultValue(true).build());
        this.CheckMine = (Setting<Boolean>)this.sgGeneral.add((Setting) new BoolSetting.Builder().name("CheckMine").description("11").defaultValue(true).build());
        this.CheckSelf = (Setting<Boolean>)this.sgGeneral.add((Setting) new BoolSetting.Builder().name("CheckSelf").description("11").defaultValue(true).build());
        this.CheckFriend = (Setting<Boolean>)this.sgGeneral.add((Setting) new BoolSetting.Builder().name("CheckFriend").description("11").defaultValue(true).build());
        this.range = (Setting<Double>)this.sgGeneral.add((Setting) new DoubleSetting.Builder().name("target-range").description("The maximum distance to target players.").defaultValue(3.5).range(0.0, 8.0).sliderMax(8.0).build());
        this.multiPlace = (Setting<Integer>)this.sgGeneral.add((Setting) new IntSetting.Builder().name("MultiPlace").defaultValue(5).min(1).sliderRange(1, 5).build());
        this.delay = (Setting<Integer>)this.sgGeneral.add((Setting) new IntSetting.Builder().name("Delay").defaultValue(35).min(0).sliderRange(0, 2000).build());
        this.targets = new ArrayList<PlayerEntity>();
        this.render = (Setting<Boolean>)this.sgRender.add((Setting) new BoolSetting.Builder().name("Render").description("").defaultValue(true).build());
        this.renderTime = (Setting<Double>)this.sgRender.add((Setting) new DoubleSetting.Builder().name("Render Time").description("How long the box should remain in full alpha.").defaultValue(0.3).min(0.0).sliderRange(0.0, 10.0).build());
        this.fadeTime = (Setting<Double>)this.sgRender.add((Setting) new DoubleSetting.Builder().name("Fade Time").description("How long the fading should take.").defaultValue(1.0).min(0.0).sliderRange(0.0, 10.0).build());
        final SettingGroup sgRender = this.sgRender;
        final EnumSetting.Builder builder = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)new EnumSetting.Builder().name("Shape Mode")).description("Which parts of the boxes should be rendered.")).defaultValue(ShapeMode.Sides);
        final Setting<Boolean> render = this.render;
        Objects.requireNonNull(render);
        this.shapeMode = (Setting<ShapeMode>)sgRender.add(((EnumSetting.Builder)builder.visible(render::get)).build());
        this.lineColor = (Setting<SettingColor>)this.sgRender.add((Setting) new ColorSetting.Builder().name("Line Color").description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness").defaultValue(new SettingColor(255, 255, 255, 255)).visible(() -> this.render.get() && (this.shapeMode.get().equals(ShapeMode.Lines) || this.shapeMode.get().equals(ShapeMode.Both))).build());
        this.sideColor = (Setting<SettingColor>)this.sgRender.add((Setting) new ColorSetting.Builder().name("Side Color").description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness").defaultValue(new SettingColor(255, 255, 255, 20)).visible(() -> this.render.get() && (this.shapeMode.get().equals(ShapeMode.Sides) || this.shapeMode.get().equals(ShapeMode.Both))).build());
        this.progress = 0;
        this.timer = new Timer();
        MeteorClient.EVENT_BUS.subscribe(new Renderer());
    }

    @EventHandler(priority = 200)
    private void onTickPre(final TickEvent.Post event) {
        this.updateTargets();
    }

    public String getInfoString() {
        for (final PlayerEntity target : this.targets) {
            if (target != null) {
                return target.getGameProfile().getName();
            }
        }
        return null;
    }

    @EventHandler
    private void onTick(final TickEvent.Pre event) {
        if (this.timer.passedMs(this.delay.get()) && (!(boolean)this.onlyGround.get() || this.mc.player.isOnGround()) && this.pauseCheck() && InvUtils.find(Items.COBWEB).found()) {
            this.progress = 0;
            for (final PlayerEntity target : this.targets) {
                if (this.down.get()) {
                    this.placeWeb(this.getPlaceBlock(target, -1.0));
                }
                if (this.face.get()) {
                    this.placeWeb(this.getPlaceBlock(target, 1.0));
                }
                if (this.feet.get()) {
                    if (target.speed < this.minSpeed.get() || this.surCheck(target)) {
                        return;
                    }
                    this.placeWeb(this.getPlaceBlock(target, 0.0));
                }
            }
        }
    }

    private static boolean isWeb(final BlockPos pos) {
        return Util.mc.world != null && Util.mc.player != null && pos != null && (Util.mc.world.getBlockState(pos).getBlock() == Blocks.COBWEB || Util.mc.player.getBlockPos().equals(pos));
    }

    public boolean isInWeb(final PlayerEntity player) {
        return isWeb(this.getPlaceBlock(player, -1.0)) || isWeb(this.getPlaceBlock(player, 0.0)) || isWeb(this.getPlaceBlock(player, 1.0));
    }

    public boolean surCheck(final PlayerEntity player) {
        int n = 0;
        final BlockPos pos = player.getBlockPos();
        if (!BOBlockUtil.isAir(pos.add(0, 0, 1))) {
            ++n;
        }
        if (!BOBlockUtil.isAir(pos.add(0, 0, -1))) {
            ++n;
        }
        if (!BOBlockUtil.isAir(pos.add(1, 0, 0))) {
            ++n;
        }
        if (!BOBlockUtil.isAir(pos.add(-1, 0, 0))) {
            ++n;
        }
        return n > this.surCheck.get();
    }

    protected BlockPos getPlaceBlock(final PlayerEntity player, final double y) {
        final LinkedHashSet<BlockPos> feetBlock = this.getAllPos(player, y);
        final List<BlockPos> collect = feetBlock.stream().filter(BOBlockUtil::isAir).filter(p -> !BOBlockUtil.cantBlockPlace(p)).limit(1L).toList();
        return (collect.size() == 0) ? null : collect.get(0);
    }

    public LinkedHashSet<BlockPos> getAllPos(final PlayerEntity player, final double yOff) {
        final LinkedHashSet<BlockPos> set = new LinkedHashSet<BlockPos>();
        if (player != null) {
            set.add(BOBlockUtil.vec3toBlockPos(player.getPos().add(0.0, yOff, 0.0)));
            set.add(BOBlockUtil.vec3toBlockPos(player.getPos().add(0.2, yOff, 0.2)));
            set.add(BOBlockUtil.vec3toBlockPos(player.getPos().add(-0.2, yOff, 0.2)));
            set.add(BOBlockUtil.vec3toBlockPos(player.getPos().add(0.2, yOff, -0.2)));
            set.add(BOBlockUtil.vec3toBlockPos(player.getPos().add(-0.2, yOff, -0.2)));
        }
        return set;
    }

    private void updateTargets() {
        final List<PlayerEntity> players = new ArrayList<PlayerEntity>();
        double closestDist = 1000.0;
        for (int i = 3; i > 0; --i) {
            PlayerEntity closest = null;
            for (final PlayerEntity player : this.mc.world.getPlayers()) {
                if (!players.contains(player) && !Friends.get().isFriend(player) && player != this.mc.player && !player.isDead()) {
                    final double dist = player.distanceTo(this.mc.player);
                    if (dist > this.range.get() || this.surCheck(player) || (closest != null && dist >= closestDist)) {
                        continue;
                    }
                    closestDist = dist;
                    closest = player;
                }
            }
            if (closest != null) {
                players.add(closest);
            }
        }
        this.targets = players;
    }

    private void placeWeb(final BlockPos pos) {
        final PlaceData data = SettingUtils.getPlaceData(pos);
        if (data.valid() && this.progress < this.multiPlace.get() && this.mc.world.isAir(pos) && this.mc.world.isAir(pos.up()) && (!SettingUtils.shouldRotate(RotationType.BlockPlace) || Managers.ROTATION.start(data.pos(), this.priority, RotationType.BlockPlace, Objects.hash(this.name + "placing")))) {
            if ((Managers.BREAK.isMine(pos, true) && this.CheckMine.get()) || this.isSelf(pos) || this.isFriend(pos)) {
                return;
            }
            InvUtils.swap(InvUtils.findInHotbar(Items.COBWEB).slot(), true);
            this.renderBlocks.add(new Render(pos, System.currentTimeMillis()));
            this.placeBlock(Hand.MAIN_HAND, data.pos().toCenterPos(), data.dir(), data.pos());
            if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
                Managers.ROTATION.end(Objects.hash(this.name + "placing"));
            }
            InvUtils.swapBack();
            ++this.progress;
            this.timer.reset();
        }
    }

    private boolean isSelf(final BlockPos pos) {
        if (!(boolean)this.CheckSelf.get()) {
            return false;
        }
        for (final Object entity : Util.mc.world.getNonSpectatingEntities(Entity.class, new Box(pos))) {
            if (entity != Util.mc.player) {
                continue;
            }
            return true;
        }
        return false;
    }

    private boolean isFriend(final BlockPos pos) {
        if (!(boolean)this.CheckFriend.get()) {
            return false;
        }
        for (final Object entity : Util.mc.world.getNonSpectatingEntities((Class)PlayerEntity.class, new Box(pos))) {
            if (!Friends.get().isFriend((PlayerEntity) entity)) {
                continue;
            }
            return true;
        }
        return false;
    }

    private boolean pauseCheck() {
        return !(boolean)this.pauseEat.get() || !this.mc.player.isUsingItem();
    }

    record Render(BlockPos blockPos, long time) {}

    private class Renderer
    {
        @EventHandler
        private void onRender(final Render3DEvent event) {
            if (!(boolean)SmartWeb.this.render.get()) {
                return;
            }
            SmartWeb.this.renderBlocks.removeIf(r -> System.currentTimeMillis() - r.time > 1000L);
            SmartWeb.this.renderBlocks.forEach(r -> {
                final double progress = 1.0 - Math.min(System.currentTimeMillis() - r.time + SmartWeb.this.renderTime.get() * 1000.0, SmartWeb.this.fadeTime.get() * 1000.0) / (SmartWeb.this.fadeTime.get() * 1000.0);
                event.renderer.box(r.blockPos, RenderUtils.injectAlpha(SmartWeb.this.sideColor.get(), (int)Math.round(SmartWeb.this.sideColor.get().a * progress)), RenderUtils.injectAlpha(SmartWeb.this.lineColor.get(), (int)Math.round(SmartWeb.this.lineColor.get().a * progress)), SmartWeb.this.shapeMode.get(), 0);
            });
        }
    }
}
