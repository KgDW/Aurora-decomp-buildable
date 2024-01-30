package kassuk.addon.aurora.modules;

import kassuk.addon.aurora.utils.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Iterator;
import net.minecraft.entity.decoration.EndCrystalEntity;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import net.minecraft.util.math.Direction;
import java.util.LinkedList;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import kassuk.addon.aurora.enums.SwingType;
import kassuk.addon.aurora.enums.SwingState;
import kassuk.addon.aurora.utils.EntityInfo;
import kassuk.addon.aurora.utils.BOBlockUtil;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.Vec3d;
import kassuk.addon.aurora.utils.PlaceData;
import net.minecraft.util.math.BlockPos;
import kassuk.addon.aurora.utils.BOInvUtils;
import kassuk.addon.aurora.enums.RotationType;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.util.Hand;
import kassuk.addon.aurora.managers.Managers;
import kassuk.addon.aurora.utils.SettingUtils;
import meteordevelopment.meteorclient.events.world.TickEvent;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.settings.ColorSetting;
import java.util.Objects;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import net.minecraft.block.Blocks;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import java.util.ArrayList;
import kassuk.addon.aurora.BlackOut;
import net.minecraft.item.ItemStack;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import kassuk.addon.aurora.enums.SwingHand;
import net.minecraft.block.Block;
import meteordevelopment.meteorclient.settings.Setting;
import java.util.List;
import meteordevelopment.meteorclient.settings.SettingGroup;
import kassuk.addon.aurora.BlackOutModule;

public class BurrowPlus2 extends BlackOutModule
{
    private final SettingGroup sgGeneral;
    private final SettingGroup sgAttack;
    private final SettingGroup sgRender;
    private final List<Render> renderBlocks;
    private final Setting<SwitchMode> switchMode;
    private final Setting<LagBackMode> lagBackMode;
    private final Setting<List<Block>> blocks;
    private final Setting<Boolean> multiPlace;
    private final Setting<Boolean> lagBack;
    private final Setting<Boolean> fillHead;
    private final Setting<Double> attackSpeed;
    private final Setting<Boolean> placeSwing;
    private final Setting<SwingHand> placeHand;
    private final Setting<Boolean> render;
    private final Setting<Double> renderTime;
    private final Setting<Double> fadeTime;
    private final Setting<ShapeMode> shapeMode;
    private final Setting<SettingColor> lineColor;
    private final Setting<SettingColor> sideColor;
    private long lastAttack;
    private final Predicate<ItemStack> predicate;

    public BurrowPlus2() {
        super(BlackOut.BLACKOUT, "BestBurrow", "Places a block inside your feet.");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.sgAttack = this.settings.createGroup("Attack");
        this.sgRender = this.settings.createGroup("Render");
        this.renderBlocks = new ArrayList<Render>();
        this.switchMode = (Setting<SwitchMode>)this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)new EnumSetting.Builder().name("Switch Mode")).description("The mode to switch obsidian.")).defaultValue(SwitchMode.Silent)).build());
        this.lagBackMode = (Setting<LagBackMode>)this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)new EnumSetting.Builder().name("LagBack Mode")).description("")).defaultValue(LagBackMode.XIN)).build());
        this.blocks = (Setting<List<Block>>)this.sgGeneral.add((Setting) new BlockListSetting.Builder().name("Block To Use").description("Which blocks used for burrow.").defaultValue(new Block[] { Blocks.OBSIDIAN, Blocks.ENDER_CHEST }).build());
        this.multiPlace = (Setting<Boolean>)this.sgGeneral.add((Setting) new BoolSetting.Builder().name("Multi Place").description("bypass2?.").defaultValue(true).build());
        this.lagBack = (Setting<Boolean>)this.sgGeneral.add((Setting) new BoolSetting.Builder().name("Lag Back").description("bypass2.").defaultValue(true).build());
        this.fillHead = (Setting<Boolean>)this.sgGeneral.add((Setting) new BoolSetting.Builder().name("Fill Head").description("MaoJunQing").defaultValue(false).build());
        this.attackSpeed = (Setting<Double>)this.sgAttack.add((Setting) new DoubleSetting.Builder().name("Attack Speed").description("How many times to attack every second.").defaultValue(4.0).min(0.0).sliderRange(0.0, 20.0).build());
        this.placeSwing = (Setting<Boolean>)this.sgRender.add((Setting) new BoolSetting.Builder().name("Swing").description("Renders swing animation when placing a block.").defaultValue(true).build());
        final SettingGroup sgRender = this.sgRender;
        final EnumSetting.Builder builder = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)new EnumSetting.Builder().name("Swing Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
        final Setting<Boolean> placeSwing = this.placeSwing;
        Objects.requireNonNull(placeSwing);
        this.placeHand = (Setting<SwingHand>)sgRender.add(((EnumSetting.Builder)builder.visible(placeSwing::get)).build());
        this.render = (Setting<Boolean>)this.sgRender.add((Setting) new BoolSetting.Builder().name("Render").description("").defaultValue(true).build());
        this.renderTime = (Setting<Double>)this.sgRender.add((Setting) new DoubleSetting.Builder().name("Render Time").description("How long the box should remain in full alpha.").defaultValue(0.3).min(0.0).sliderRange(0.0, 10.0).build());
        this.fadeTime = (Setting<Double>)this.sgRender.add((Setting) new DoubleSetting.Builder().name("Fade Time").description("How long the fading should take.").defaultValue(1.0).min(0.0).sliderRange(0.0, 10.0).build());
        final SettingGroup sgRender2 = this.sgRender;
        final EnumSetting.Builder builder2 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)new EnumSetting.Builder().name("Shape Mode")).description("Which parts of the boxes should be rendered.")).defaultValue(ShapeMode.Sides);
        final Setting<Boolean> render = this.render;
        Objects.requireNonNull(render);
        this.shapeMode = (Setting<ShapeMode>)sgRender2.add(((EnumSetting.Builder)builder2.visible(render::get)).build());
        this.lineColor = (Setting<SettingColor>)this.sgRender.add((Setting) new ColorSetting.Builder().name("Line Color").description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness").defaultValue(new SettingColor(255, 255, 255, 255)).visible(() -> this.render.get() && (this.shapeMode.get().equals(ShapeMode.Lines) || this.shapeMode.get().equals(ShapeMode.Both))).build());
        this.sideColor = (Setting<SettingColor>)this.sgRender.add((Setting) new ColorSetting.Builder().name("Side Color").description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness").defaultValue(new SettingColor(255, 255, 255, 20)).visible(() -> this.render.get() && (this.shapeMode.get().equals(ShapeMode.Sides) || this.shapeMode.get().equals(ShapeMode.Both))).build());
        MeteorClient.EVENT_BUS.subscribe(new Renderer());
        this.lastAttack = 0L;
        this.predicate = (itemStack -> {
            final Item patt5513$temp = itemStack.getItem();
            if (patt5513$temp instanceof final BlockItem block) {
                return this.blocks.get().contains(block.getBlock());
            }
            else {
                return false;
            }
        });
    }

    @EventHandler
    private void onTick(final TickEvent.Pre event) {
        if (this.mc.player != null && this.mc.world != null && this.mc.player.isOnGround()) {
            final BlockPos selfPos = this.getFillBlock();
            if (selfPos == null) {
                this.toggle();
                this.sendToggledMsg();
            }
            else {
                final PlaceData data = SettingUtils.getPlaceData(selfPos);
                if (data.valid()) {
                    final boolean headFillMode = selfPos.getY() > this.mc.player.getY();
                    final List<Vec3d> fakeJumpOffsets = this.getFakeJumpOffset(selfPos, headFillMode);
                    if (fakeJumpOffsets.size() != 4) {
                        this.toggle();
                    }
                    else {
                        final Hand hand = this.predicate.test(Managers.HOLDING.getStack()) ? Hand.MAIN_HAND : (this.predicate.test(this.mc.player.getOffHandStack()) ? Hand.OFF_HAND : null);
                        boolean blocksPresent = hand != null;
                        if (!blocksPresent) {
                            switch (this.switchMode.get().ordinal()) {
                                case 1:
                                case 2: {
                                    blocksPresent = InvUtils.findInHotbar(this.predicate).found();
                                    break;
                                }
                                case 3:
                                case 4: {
                                    blocksPresent = InvUtils.find(this.predicate).found();
                                    break;
                                }
                            }
                        }
                        if (blocksPresent) {
                            this.attackCrystal(selfPos);
                            if (!SettingUtils.shouldRotate(RotationType.BlockPlace) || Managers.ROTATION.start(data.pos(), this.priority, RotationType.BlockPlace, Objects.hash(this.name + "placing"))) {
                                boolean switched = hand != null;
                                if (!switched) {
                                    switched = switch (this.switchMode.get().ordinal()) {
                                        case 1,  2 -> InvUtils.swap(InvUtils.findInHotbar(this.predicate).slot(), true);
                                        case 3 -> BOInvUtils.pickSwitch(InvUtils.find(this.predicate).slot());
                                        case 4 -> BOInvUtils.invSwitch(InvUtils.find(this.predicate).slot());
                                        default -> throw new IncompatibleClassChangeError();
                                    };
                                }
                                if (switched) {
                                    this.doFakeJump(fakeJumpOffsets);
                                    if (this.multiPlace.get()) {
                                        this.multiPlace(headFillMode);
                                    }
                                    else {
                                        this.placeBlock(Hand.MAIN_HAND, data.pos().toCenterPos(), data.dir(), data.pos());
                                    }
                                    if (this.placeSwing.get()) {
                                        this.clientSwing(this.placeHand.get(), Hand.MAIN_HAND);
                                    }
                                    final BlockPos yxPos = this.mc.player.getBlockPos();
                                    if (this.lagBack.get()) {
                                        this.doLagBack(yxPos);
                                    }
                                    switch (this.switchMode.get().ordinal()) {
                                        case 2: {
                                            InvUtils.swapBack();
                                            break;
                                        }
                                        case 3: {
                                            BOInvUtils.pickSwapBack();
                                            break;
                                        }
                                        case 4: {
                                            BOInvUtils.swapBack();
                                            break;
                                        }
                                    }
                                    if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
                                        Managers.ROTATION.end(Objects.hash(this.name + "placing"));
                                    }
                                }
                                this.toggle();
                                this.sendToggledMsg();
                            }
                        }
                    }
                }
            }
        }
    }

    private void multiPlace(final boolean headFillMode) {
        if (BOBlockUtil.isAir(BOBlockUtil.vec3toBlockPos(this.mc.player.getPos().add(0.0, 0.0, 0.0)))) {
            this.mPlace(Hand.MAIN_HAND, BOBlockUtil.vec3toBlockPos(this.mc.player.getPos().add(0.0, 0.0, 0.0)));
        }
        if (BOBlockUtil.isAir(BOBlockUtil.vec3toBlockPos(this.mc.player.getPos().add(0.3, 0.0, 0.3)))) {
            this.mPlace(Hand.MAIN_HAND, BOBlockUtil.vec3toBlockPos(this.mc.player.getPos().add(0.3, 0.0, 0.3)));
        }
        if (BOBlockUtil.isAir(BOBlockUtil.vec3toBlockPos(this.mc.player.getPos().add(-0.3, 0.0, 0.3)))) {
            this.mPlace(Hand.MAIN_HAND, BOBlockUtil.vec3toBlockPos(this.mc.player.getPos().add(-0.3, 0.0, 0.3)));
        }
        if (BOBlockUtil.isAir(BOBlockUtil.vec3toBlockPos(this.mc.player.getPos().add(0.3, 0.0, -0.3)))) {
            this.mPlace(Hand.MAIN_HAND, BOBlockUtil.vec3toBlockPos(this.mc.player.getPos().add(0.3, 0.0, -0.3)));
        }
        if (BOBlockUtil.isAir(BOBlockUtil.vec3toBlockPos(this.mc.player.getPos().add(-0.3, 0.0, -0.3)))) {
            this.mPlace(Hand.MAIN_HAND, BOBlockUtil.vec3toBlockPos(this.mc.player.getPos().add(-0.3, 0.0, -0.3)));
        }
        if (headFillMode) {
            if (BOBlockUtil.isAir(BOBlockUtil.vec3toBlockPos(this.mc.player.getPos().add(0.0, 1.0, 0.0)))) {
                this.mPlace(Hand.MAIN_HAND, BOBlockUtil.vec3toBlockPos(this.mc.player.getPos().add(0.0, 1.0, 0.0)));
            }
            if (BOBlockUtil.isAir(BOBlockUtil.vec3toBlockPos(this.mc.player.getPos().add(0.3, 1.0, 0.3)))) {
                this.mPlace(Hand.MAIN_HAND, BOBlockUtil.vec3toBlockPos(this.mc.player.getPos().add(0.3, 1.0, 0.3)));
            }
            if (BOBlockUtil.isAir(BOBlockUtil.vec3toBlockPos(this.mc.player.getPos().add(-0.3, 1.0, 0.3)))) {
                this.mPlace(Hand.MAIN_HAND, BOBlockUtil.vec3toBlockPos(this.mc.player.getPos().add(-0.3, 1.0, 0.3)));
            }
            if (BOBlockUtil.isAir(BOBlockUtil.vec3toBlockPos(this.mc.player.getPos().add(0.3, 1.0, -0.3)))) {
                this.mPlace(Hand.MAIN_HAND, BOBlockUtil.vec3toBlockPos(this.mc.player.getPos().add(0.3, 1.0, -0.3)));
            }
            if (BOBlockUtil.isAir(BOBlockUtil.vec3toBlockPos(this.mc.player.getPos().add(-0.3, 1.0, -0.3)))) {
                this.mPlace(Hand.MAIN_HAND, BOBlockUtil.vec3toBlockPos(this.mc.player.getPos().add(-0.3, 1.0, -0.3)));
            }
        }
    }

    private void attackCrystal(final BlockPos pos) {
        if (System.currentTimeMillis() - this.lastAttack >= 1000.0 / this.attackSpeed.get() && EntityInfo.CrystalCheck(pos)) {
            final Entity blocking = this.getBlocking();
            if (blocking != null && (!SettingUtils.shouldRotate(RotationType.Attacking) || Managers.ROTATION.start(blocking.getBoundingBox(), this.priority - 0.1, RotationType.Attacking, Objects.hash(this.name + "attacking")))) {
                SettingUtils.swing(SwingState.Pre, SwingType.Attacking, Hand.MAIN_HAND);
                this.sendPacket(PlayerInteractEntityC2SPacket.attack(blocking, this.mc.player.isSneaking()));
                SettingUtils.swing(SwingState.Post, SwingType.Attacking, Hand.MAIN_HAND);
                if (SettingUtils.shouldRotate(RotationType.Attacking)) {
                    Managers.ROTATION.end(Objects.hash(this.name + "attacking"));
                }
                this.lastAttack = System.currentTimeMillis();
            }
        }
    }

    public double ez() {
        if (!BOBlockUtil.isAir(EntityInfo.playerPos(this.mc.player).up(3))) {
            return 1.2;
        }
        final double lol = 2.2;
        for (int i = 4; i < 6; ++i) {
            if (!BOBlockUtil.isAir(EntityInfo.playerPos(this.mc.player).up(i))) {
                return lol + i - 4.0;
            }
        }
        return 10.0;
    }

    private void doLagBack(final BlockPos selfPos) {
        switch (this.lagBackMode.get().ordinal()) {
            case 1: {
                for (int i = 10; i > 0; --i) {
                    if (BOBlockUtil.isAir(selfPos.add(0, i, 0)) && BOBlockUtil.isAir(selfPos.add(0, i, 0).up())) {
                        final BlockPos lagPos = selfPos.add(0, i, 0);
                        this.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(lagPos.getX() + 0.5, lagPos.getY(), lagPos.getZ() + 0.5, true));
                    }
                }
            }
            case 2: {
                this.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(this.mc.player.getX(), this.mc.player.getY() + 2.0, this.mc.player.getZ(), true));
            }
            case 3: {
                this.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(this.mc.player.getX(), this.mc.player.getY() + this.ez(), this.mc.player.getZ(), true));
            }
            default: {}
        }
    }

    private void doFakeJump(final List<Vec3d> offsets) {
        if (offsets != null) {
            offsets.forEach(vec -> {
                if (vec != null) {
                    if (!vec.equals(new Vec3d(0.0, 0.0, 0.0))) {
                        this.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(vec.x, vec.y, vec.z, true));
                    }
                }
            });
        }
    }

    private List<Vec3d> getFakeJumpOffset(final BlockPos burBlock, final boolean headFillMode) {
        final List<Vec3d> offsets = new LinkedList<Vec3d>();
        if (headFillMode) {
            if (BOBlockUtil.fakeBBoxCheckFeet(this.mc.player, new Vec3d(0.0, 2.0, 0.0))) {
                final Vec3d offVec = this.getVec3dDirection(burBlock);
                offsets.add(new Vec3d(this.mc.player.getX() + offVec.x * 0.42132, this.mc.player.getY() + 0.4199999868869781, this.mc.player.getZ() + offVec.z * 0.42132));
                offsets.add(new Vec3d(this.mc.player.getX() + offVec.x * 0.95, this.mc.player.getY() + 0.7531999805212017, this.mc.player.getZ() + offVec.z * 0.95));
                offsets.add(new Vec3d(this.mc.player.getX() + offVec.x * 1.03, this.mc.player.getY() + 0.9999957640154541, this.mc.player.getZ() + offVec.z * 1.03));
                offsets.add(new Vec3d(this.mc.player.getX() + offVec.x * 1.0933, this.mc.player.getY() + 1.1661092609382138, this.mc.player.getZ() + offVec.z * 1.0933));
            }
            else {
                final Vec3d offVec = this.getVec3dDirection(burBlock);
                offsets.add(new Vec3d(this.mc.player.getX() + offVec.x * 0.42132, this.mc.player.getY() + 0.12160004615784, this.mc.player.getZ() + offVec.z * 0.42132));
                offsets.add(new Vec3d(this.mc.player.getX() + offVec.x * 0.95, this.mc.player.getY() + 0.200000047683716, this.mc.player.getZ() + offVec.z * 0.95));
                offsets.add(new Vec3d(this.mc.player.getX() + offVec.x * 1.03, this.mc.player.getY() + 0.200000047683716, this.mc.player.getZ() + offVec.z * 1.03));
                offsets.add(new Vec3d(this.mc.player.getX() + offVec.x * 1.0933, this.mc.player.getY() + 0.12160004615784, this.mc.player.getZ() + offVec.z * 1.0933));
            }
        }
        else if (BOBlockUtil.fakeBBoxCheckFeet(this.mc.player, new Vec3d(0.0, 2.0, 0.0))) {
            offsets.add(new Vec3d(this.mc.player.getX(), this.mc.player.getY() + 0.4199999868869781, this.mc.player.getZ()));
            offsets.add(new Vec3d(this.mc.player.getX(), this.mc.player.getY() + 0.7531999805212017, this.mc.player.getZ()));
            offsets.add(new Vec3d(this.mc.player.getX(), this.mc.player.getY() + 0.9999957640154541, this.mc.player.getZ()));
            offsets.add(new Vec3d(this.mc.player.getX(), this.mc.player.getY() + 1.1661092609382138, this.mc.player.getZ()));
        }
        else {
            final Vec3d offVec = this.getVec3dDirection(burBlock);
            offsets.add(new Vec3d(this.mc.player.getX() + offVec.x * 0.42132, this.mc.player.getY() + 0.12160004615784, this.mc.player.getZ() + offVec.z * 0.42132));
            offsets.add(new Vec3d(this.mc.player.getX() + offVec.x * 0.95, this.mc.player.getY() + 0.200000047683716, this.mc.player.getZ() + offVec.z * 0.95));
            offsets.add(new Vec3d(this.mc.player.getX() + offVec.x * 1.03, this.mc.player.getY() + 0.200000047683716, this.mc.player.getZ() + offVec.z * 1.03));
            offsets.add(new Vec3d(this.mc.player.getX() + offVec.x * 1.0933, this.mc.player.getY() + 0.12160004615784, this.mc.player.getZ() + offVec.z * 1.0933));
        }
        return offsets;
    }

    public Vec3d getVec3dDirection(final BlockPos burBlockPos) {
        final Vec3d v = new Vec3d(burBlockPos.getX(), burBlockPos.getY(), burBlockPos.getZ()).add(0.5, 0.5, 0.5);
        final BlockPos pPos = BOBlockUtil.getFlooredPosition(this.mc.player);
        final Vec3d s = this.mc.player.getPos().subtract(v);
        Vec3d off = new Vec3d(0.0, 0.0, 0.0);
        if (Math.abs(s.x) >= Math.abs(s.z) && Math.abs(s.x) > 0.2) {
            if (s.x > 0.0) {
                off = new Vec3d(0.8 - s.x, 0.0, 0.0);
            }
            else {
                off = new Vec3d(-0.8 - s.x, 0.0, 0.0);
            }
        }
        else if (Math.abs(s.z) >= Math.abs(s.x) && Math.abs(s.z) > 0.2) {
            if (s.z > 0.0) {
                off = new Vec3d(0.0, 0.0, 0.8 - s.z);
            }
            else {
                off = new Vec3d(0.0, 0.0, -0.8 - s.z);
            }
        }
        else if (burBlockPos.equals(pPos)) {
            final List<Direction> facList = new ArrayList<Direction>();
            final Direction[] var7 = Direction.values();
            final int var8 = var7.length;
            for (final Direction f : var7) {
                if (f != Direction.UP && f != Direction.DOWN && BOBlockUtil.isAir(pPos.offset(f)) && BOBlockUtil.isAir(pPos.offset(f).offset(Direction.UP))) {
                    facList.add(f);
                }
            }
            facList.sort((f1, f2) -> {
                final Vec3d offVec1 = v.add(new Vec3d(f1.getUnitVector()).multiply(0.5));
                final Vec3d offVec2 = v.add(new Vec3d(f2.getUnitVector()).multiply(0.5));
                return (int)(PlayerUtils.distanceTo(offVec1.x, this.mc.player.getY(), offVec1.z) - PlayerUtils.distanceTo(offVec2.x, this.mc.player.getY(), offVec2.z));
            });
            if (facList.size() > 0) {
                off = new Vec3d(facList.get(0).getUnitVector());
            }
        }
        return off;
    }

    private Entity getBlocking() {
        Entity crystal = null;
        if (this.mc.world != null && this.mc.player != null) {
            for (final Entity entity : this.mc.world.getEntities()) {
                if (entity instanceof EndCrystalEntity && SettingUtils.inAttackRange(entity.getBoundingBox())) {
                    crystal = entity;
                }
            }
        }
        return crystal;
    }

    protected BlockPos getFillBlock() {
        final LinkedHashSet<BlockPos> feetBlock = this.getFeetBlock(0);
        final List<BlockPos> collect = feetBlock.stream().filter(BOBlockUtil::isAir).filter(p -> !BOBlockUtil.cantBlockPlace(p)).limit(1L).toList();
        return (collect.size() == 0) ? null : collect.get(0);
    }

    public LinkedHashSet<BlockPos> getFeetBlock(final int yOff) {
        final LinkedHashSet<BlockPos> set = new LinkedHashSet<BlockPos>();
        set.add(BOBlockUtil.vec3toBlockPos(this.mc.player.getPos().add(0.0, yOff, 0.0)));
        set.add(BOBlockUtil.vec3toBlockPos(this.mc.player.getPos().add(0.3, yOff, 0.3)));
        set.add(BOBlockUtil.vec3toBlockPos(this.mc.player.getPos().add(-0.3, yOff, 0.3)));
        set.add(BOBlockUtil.vec3toBlockPos(this.mc.player.getPos().add(0.3, yOff, -0.3)));
        set.add(BOBlockUtil.vec3toBlockPos(this.mc.player.getPos().add(-0.3, yOff, -0.3)));
        if (this.fillHead.get() && yOff == 0) {
            set.addAll(this.getFeetBlock(1));
        }
        return set;
    }

    public void mPlace(final Hand hand, final BlockPos pos) {
        final Vec3d eyes = this.mc.player.getEyePos();
        final boolean inside = eyes.x > pos.getX() && eyes.x < pos.getX() + 1 && eyes.y > pos.getY() && eyes.y < pos.getY() + 1 && eyes.z > pos.getZ() && eyes.z < pos.getZ() + 1;
        final PlaceData data = SettingUtils.getPlaceData(pos);
        if (data.valid()) {
            this.renderBlocks.add(new Render(pos, System.currentTimeMillis()));
            SettingUtils.swing(SwingState.Pre, SwingType.Placing, hand);
            this.sendSequenced(s -> new PlayerInteractBlockC2SPacket(hand, new BlockHitResult(data.pos().toCenterPos(), data.dir(), data.pos(), inside), s));
            SettingUtils.swing(SwingState.Post, SwingType.Placing, hand);
        }
    }

    public enum SwitchMode
    {
        Normal("Normal", 0),
        Silent("Silent", 1),
        PickSilent("PickSilent", 2),
        InvSwitch("InvSwitch", 3);

        SwitchMode(final String string, final int i) {
        }
    }

    public enum LagBackMode
    {
        OBS("OBS", 0),
        XIN("XIN", 1),
        OLD("OLD", 2);

        LagBackMode(final String string, final int i) {
        }
    }

    record Render(BlockPos blockPos, long time) {}

    private class Renderer
    {
        @EventHandler
        private void onRender(final Render3DEvent event) {
            if (!(boolean)BurrowPlus2.this.render.get()) {
                return;
            }
            BurrowPlus2.this.renderBlocks.removeIf(r -> System.currentTimeMillis() - r.time > 1000L);
            BurrowPlus2.this.renderBlocks.forEach(r -> {
                final double progress = 1.0 - Math.min(System.currentTimeMillis() - r.time + BurrowPlus2.this.renderTime.get() * 1000.0, BurrowPlus2.this.fadeTime.get() * 1000.0) / (BurrowPlus2.this.fadeTime.get() * 1000.0);
                event.renderer.box(r.blockPos, RenderUtils.injectAlpha(BurrowPlus2.this.sideColor.get(), (int)Math.round(BurrowPlus2.this.sideColor.get().a * progress)), RenderUtils.injectAlpha(BurrowPlus2.this.lineColor.get(), (int)Math.round(BurrowPlus2.this.lineColor.get().a * progress)), BurrowPlus2.this.shapeMode.get(), 0);
            });
        }
    }
}
