package kassuk.addon.aurora.modules;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import kassuk.addon.aurora.utils.BOInvUtils;
import kassuk.addon.aurora.enums.RotationType;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import kassuk.addon.aurora.managers.Managers;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3i;
import net.minecraft.block.Blocks;
import kassuk.addon.aurora.utils.OLEPOSSUtils;
import java.util.Iterator;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import kassuk.addon.aurora.utils.meteor.BODamageUtils;
import net.minecraft.entity.Entity;
import kassuk.addon.aurora.utils.CrystalUtil;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.util.math.Box;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.orbit.EventHandler;
import kassuk.addon.aurora.utils.SettingUtils;
import meteordevelopment.meteorclient.events.world.TickEvent;
import java.util.HashMap;
import java.util.ArrayList;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import java.util.Objects;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import kassuk.addon.aurora.BlackOut;
import java.util.Map;
import net.minecraft.entity.player.PlayerEntity;
import java.util.List;
import kassuk.addon.aurora.utils.PlaceData;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import kassuk.addon.aurora.enums.SwingHand;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import org.joml.Vector3d;
import kassuk.addon.aurora.BlackOutModule;

public class AutoAnchor extends BlackOutModule
{
    private double renderProgress;
    private long lastMillis;
    double dmg;
    double self;
    Vector3d vec;
    private final SettingGroup sgGeneral;
    private final SettingGroup sgDamage;
    private final SettingGroup sgRender;
    private final SettingGroup sgDev;
    private final Setting<Boolean> pauseEat;
    private final Setting<SwitchMode> switchMode;
    private final Setting<LogicMode> logicMode;
    private final Setting<Double> speed;
    private final Setting<Double> minDmg;
    private final Setting<Double> maxDmg;
    private final Setting<Double> minRatio;
    private final Setting<Boolean> placeSwing;
    private final Setting<SwingHand> placeHand;
    private final Setting<Boolean> interactSwing;
    private final Setting<SwingHand> interactHand;
    private final Setting<Boolean> damage;
    private final Setting<Double> damageScale;
    private final Setting<SettingColor> damageColor;
    private final Setting<FadeMode> fadeMode;
    private final Setting<Double> animationSpeed;
    private final Setting<Double> animationMoveExponent;
    private final Setting<Double> animationExponent;
    private final Setting<ShapeMode> shapeMode;
    private final Setting<SettingColor> lineColor;
    public final Setting<SettingColor> color;
    private final Setting<Integer> Predict;
    private final Setting<Integer> Radius;
    private BlockPos[] blocks;
    private int lastIndex;
    private int length;
    private long tickTime;
    private double bestDmg;
    private final long lastTime;
    private Vec3d renderTarget;
    private BlockPos placePos;
    private PlaceData placeData;
    private BlockPos calcPos;
    private PlaceData calcData;
    private Vec3d renderPos;
    private List<PlayerEntity> targets;
    private final Map<BlockPos, Anchor> anchors;
    double timer;

    public AutoAnchor() {
        super(BlackOut.BLACKOUT, "MaoJunQing Aura", "Automatically destroys people using anchors.");
        this.renderProgress = 0.0;
        this.lastMillis = System.currentTimeMillis();
        this.sgGeneral = this.settings.getDefaultGroup();
        this.sgDamage = this.settings.createGroup("Damage");
        this.sgRender = this.settings.createGroup("Render");
        this.sgDev = this.settings.createGroup("Dev");
        this.pauseEat = (Setting<Boolean>)this.sgGeneral.add((Setting) new BoolSetting.Builder().name("Pause Eat").description("Pauses when you are eating.").defaultValue(true).build());
        this.switchMode = (Setting<SwitchMode>)this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)new EnumSetting.Builder().name("Switch Mode")).description("Switching method. Silent is the most reliable but doesn't work everywhere.")).defaultValue(SwitchMode.Silent)).build());
        this.logicMode = (Setting<LogicMode>)this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)new EnumSetting.Builder().name("Logic Mode")).description("Logic for bullying kids.")).defaultValue(LogicMode.BreakPlace)).build());
        this.speed = (Setting<Double>)this.sgGeneral.add((Setting) new DoubleSetting.Builder().name("Speed").description("How many anchors should be blown every second.").defaultValue(2.0).min(0.0).sliderRange(0.0, 20.0).build());
        this.minDmg = (Setting<Double>)this.sgDamage.add((Setting) new DoubleSetting.Builder().name("Min Damage").description("Minimum damage required to place.").defaultValue(8.0).min(0.0).sliderRange(0.0, 20.0).build());
        this.maxDmg = (Setting<Double>)this.sgDamage.add((Setting) new DoubleSetting.Builder().name("Max Damage").description("Maximum damage to self.").defaultValue(6.0).min(0.0).sliderRange(0.0, 20.0).build());
        this.minRatio = (Setting<Double>)this.sgDamage.add((Setting) new DoubleSetting.Builder().name("Min Damage Ratio").description("Damage ratio between enemy damage and self damage (enemy / self).").defaultValue(2.0).min(0.0).sliderRange(0.0, 10.0).build());
        this.placeSwing = (Setting<Boolean>)this.sgRender.add((Setting) new BoolSetting.Builder().name("Place Swing").description("Renders swing animation when placing a block.").defaultValue(true).build());
        final SettingGroup sgRender = this.sgRender;
        final EnumSetting.Builder builder = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)new EnumSetting.Builder().name("Place Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
        final Setting<Boolean> placeSwing = this.placeSwing;
        Objects.requireNonNull(placeSwing);
        this.placeHand = (Setting<SwingHand>)sgRender.add(((EnumSetting.Builder)builder.visible(placeSwing::get)).build());
        this.interactSwing = (Setting<Boolean>)this.sgRender.add((Setting) new BoolSetting.Builder().name("Interact Swing").description("Renders swing animation when interacting with a block.").defaultValue(true).build());
        final SettingGroup sgRender2 = this.sgRender;
        final EnumSetting.Builder builder2 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)new EnumSetting.Builder().name("Interact Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
        final Setting<Boolean> interactSwing = this.interactSwing;
        Objects.requireNonNull(interactSwing);
        this.interactHand = (Setting<SwingHand>)sgRender2.add(((EnumSetting.Builder)builder2.visible(interactSwing::get)).build());
        this.damage = (Setting<Boolean>)this.sgRender.add((Setting) new BoolSetting.Builder().name("Render Damage").description("Renders Damage.").defaultValue(true).build());
        final SettingGroup sgRender3 = this.sgRender;
        final DoubleSetting.Builder sliderMax = new DoubleSetting.Builder().name("damage-scale").description("How big the damage text should be.").defaultValue(1.25).min(1.0).sliderMax(4.0);
        final Setting<Boolean> damage = this.damage;
        Objects.requireNonNull(damage);
        this.damageScale = (Setting<Double>)sgRender3.add((Setting) sliderMax.visible(damage::get).build());
        final SettingGroup sgRender4 = this.sgRender;
        final ColorSetting.Builder defaultValue = new ColorSetting.Builder().name("Render Damage").description("Renders Damage.").defaultValue(new SettingColor(255, 255, 255, 255));
        final Setting<Boolean> damage2 = this.damage;
        Objects.requireNonNull(damage2);
        this.damageColor = (Setting<SettingColor>)sgRender4.add((Setting) defaultValue.visible(damage2::get).build());
        this.fadeMode = (Setting<FadeMode>)this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)new EnumSetting.Builder().name("Fade Mode")).description("How long the fading should take.")).defaultValue(FadeMode.Normal)).build());
        this.animationSpeed = (Setting<Double>)this.sgRender.add((Setting) new DoubleSetting.Builder().name("Animation Move Speed").description("How fast should aurora mode box move.").defaultValue(1.0).min(0.0).sliderRange(0.0, 10.0).build());
        this.animationMoveExponent = (Setting<Double>)this.sgRender.add((Setting) new DoubleSetting.Builder().name("Animation Move Exponent").description("Moves faster when longer away from the target.").defaultValue(2.0).min(0.0).sliderRange(0.0, 10.0).build());
        this.animationExponent = (Setting<Double>)this.sgRender.add((Setting) new DoubleSetting.Builder().name("Animation Exponent").description("How fast should aurora mode box grow.").defaultValue(3.0).min(0.0).sliderRange(0.0, 10.0).build());
        this.shapeMode = (Setting<ShapeMode>)this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)new EnumSetting.Builder().name("Shape Mode")).description("Which parts of render should be rendered.")).defaultValue(ShapeMode.Both)).build());
        this.lineColor = (Setting<SettingColor>)this.sgRender.add((Setting) new ColorSetting.Builder().name("Line Color").description("Line color of rendered boxes").defaultValue(new SettingColor(255, 0, 0, 255)).build());
        this.color = (Setting<SettingColor>)this.sgRender.add((Setting) new ColorSetting.Builder().name("Side Color").description("Side color of rendered boxes").defaultValue(new SettingColor(255, 0, 0, 50)).build());
        this.Predict = (Setting<Integer>)this.sgDev.add((Setting) new IntSetting.Builder().name("PredictTicks").description("PredictTicks.").defaultValue(2).min(0).sliderRange(0, 10).build());
        this.Radius = (Setting<Integer>)this.sgDev.add((Setting) new IntSetting.Builder().name("Radius").description("Radius.").defaultValue(2).min(0).sliderRange(0, 10).build());
        this.blocks = new BlockPos[0];
        this.lastIndex = 0;
        this.length = 0;
        this.tickTime = -1L;
        this.bestDmg = -1.0;
        this.lastTime = 0L;
        this.renderTarget = null;
        this.placePos = null;
        this.placeData = null;
        this.calcPos = null;
        this.calcData = null;
        this.renderPos = null;
        this.targets = new ArrayList<PlayerEntity>();
        this.anchors = new HashMap<BlockPos, Anchor>();
        this.timer = 0.0;
    }

    @EventHandler(priority = 200)
    private void onTickPre(final TickEvent.Post event) {
        this.sample(this.length - 1);
        this.placePos = this.calcPos;
        this.placeData = this.calcData;
        this.blocks = this.getBlocks(this.mc.player.getEyePos(), Math.max(SettingUtils.getPlaceRange(), SettingUtils.getPlaceWallsRange()));
        this.tickTime = System.currentTimeMillis();
        this.length = this.blocks.length;
        this.lastIndex = 0;
        this.bestDmg = -1.0;
        this.calcPos = null;
        this.calcData = null;
        this.updateTargets();
    }

    public void onActivate() {
        super.onActivate();
        this.renderPos = null;
        this.renderProgress = 0.0;
        this.lastMillis = System.currentTimeMillis();
    }

    @EventHandler(priority = 201)
    private void onRender(final Render3DEvent event) {
        final double delta = (System.currentTimeMillis() - this.lastMillis) / 1000.0f;
        this.timer += delta;
        this.lastMillis = System.currentTimeMillis();
        if (this.tickTime < 0L || this.mc.player == null || this.mc.world == null) {
            return;
        }
        if (this.pauseCheck()) {
            this.update();
        }
        if (this.placePos != null && this.pauseCheck()) {
            this.renderProgress = Math.min(1.0, this.renderProgress + delta);
            this.renderTarget = new Vec3d(this.placePos.getX(), this.placePos.getY(), this.placePos.getZ()).add(0.0, 1.0, 0.0);
        }
        else {
            this.renderProgress = Math.max(0.0, this.renderProgress - delta);
        }
        if (this.renderTarget != null) {
            this.renderPos = this.smoothMove(this.renderPos, this.renderTarget, delta * this.animationSpeed.get() * 5.0);
        }
        if (this.renderPos != null) {
            final double r = 0.5 - Math.pow(1.0 - this.renderProgress, this.animationExponent.get()) / 2.0;
            if (r >= 0.001) {
                double down = -0.5;
                double up = -0.5;
                double width = 0.5;
                int a = 0;
                switch (this.fadeMode.get()) {
                    case Up: {
                        up = 0.0;
                        down = -(r * 2.0);
                        break;
                    }
                    case Down: {
                        up = -1.0 + r * 2.0;
                        down = -1.0;
                        break;
                    }
                    case Normal: {
                        up = -0.5 + r;
                        down = -0.5 - r;
                        width = r;
                        break;
                    }
                    case Test: {
                        up = 0.0;
                        down = -1.0;
                        a = (int)(-r * 100.0);
                        break;
                    }
                }
                final Box box = new Box(this.renderPos.getX() + 0.5 - width, this.renderPos.getY() + down, this.renderPos.getZ() + 0.5 - width, this.renderPos.getX() + 0.5 + width, this.renderPos.getY() + up, this.renderPos.getZ() + 0.5 + width);
                event.renderer.box(box, new Color(this.color.get().r, this.color.get().g, this.color.get().b, this.color.get().a - a), this.lineColor.get(), this.shapeMode.get(), 0);
            }
        }
    }

    @EventHandler
    private void onRender2D(final Render2DEvent event) {
        if (this.tickTime < 0L || this.mc.player == null || this.mc.world == null) {
            return;
        }
        if (this.placePos != null && this.pauseCheck()) {
            this.vec = new Vector3d(this.renderPos.getX() + 0.5, this.renderPos.getY() - 0.5, this.renderPos.getZ() + 0.5);
        }
        if (this.vec != null && NametagUtils.to2D(this.vec, this.damageScale.get())) {
            NametagUtils.begin(this.vec);
            TextRenderer.get().begin(1.0, false, true);
            final String text = String.format("%.1f", this.dmg) + "/" + String.format("%.1f", this.self);
            final double w = TextRenderer.get().getWidth(text) * 0.5;
            TextRenderer.get().render(text, -w, 0.0, this.damageColor.get(), true);
            TextRenderer.get().end();
            NametagUtils.end();
        }
    }

    private boolean pauseCheck() {
        return !(boolean)this.pauseEat.get() || !this.mc.player.isUsingItem();
    }

    private void sample(final int index) {
        for (int i = this.lastIndex; i < index; ++i) {
            final BlockPos pos = this.blocks[i];
            this.dmg = this.getDmg(pos);
            this.self = BODamageUtils.anchorDamage(this.mc.player, this.mc.player.getBoundingBox().offset(CrystalUtil.getMotionVec(this.mc.player, this.Predict.get(), true)), pos);
            if (this.dmgCheck(this.dmg, this.self)) {
                final PlaceData data = SettingUtils.getPlaceData(pos);
                if (data.valid()) {
                    if (!EntityUtils.intersectsWithEntity(new Box(pos), entity -> !(entity instanceof ItemEntity))) {
                        this.calcData = data;
                        this.calcPos = pos;
                        this.bestDmg = this.dmg;
                    }
                }
            }
        }
        this.lastIndex = index;
    }

    private void updateTargets() {
        final List<PlayerEntity> players = new ArrayList<PlayerEntity>();
        double closestDist = 1000.0;
        for (int i = 3; i > 0; --i) {
            PlayerEntity closest = null;
            for (final PlayerEntity player : this.mc.world.getPlayers()) {
                if (!players.contains(player) && !Friends.get().isFriend(player)) {
                    if (player == this.mc.player) {
                        continue;
                    }
                    final double dist = player.distanceTo(this.mc.player);
                    if (dist > 15.0) {
                        continue;
                    }
                    if (closest != null && dist >= closestDist) {
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

    private BlockPos[] getBlocks(final Vec3d middle, final double radius) {
        final ArrayList<BlockPos> result = new ArrayList<BlockPos>();
        for (int i = (int)Math.ceil(radius), x = -i; x <= i; ++x) {
            for (int y = -i; y <= i; ++y) {
                for (int z = -i; z <= i; ++z) {
                    final BlockPos pos = new BlockPos((int)(Math.floor(middle.x) + x), (int)(Math.floor(middle.y) + y), (int)(Math.floor(middle.z) + z));
                    if (OLEPOSSUtils.replaceable(pos) || this.mc.world.getBlockState(pos).getBlock() == Blocks.RESPAWN_ANCHOR) {
                        if (this.inRangeToTargets(pos)) {
                            if (SettingUtils.inPlaceRange(pos)) {
                                result.add(pos);
                            }
                        }
                    }
                }
            }
        }
        return result.toArray(new BlockPos[0]);
    }

    private boolean inRangeToTargets(final BlockPos pos) {
        for (final PlayerEntity target : this.targets) {
            if (target.getPos().add(0.0, 1.0, 0.0).distanceTo(Vec3d.ofCenter(pos)) < 3.5) {
                return true;
            }
        }
        return false;
    }

    private void update() {
        if (this.placePos == null || this.placeData == null || !this.placeData.valid()) {
            return;
        }
        final Anchor anchor = this.getAnchor(this.placePos);
        if (this.logicMode.get() == LogicMode.PlaceBreak) {
            switch (anchor.state) {
                case Anchor: {
                    if (this.chargeUpdate(this.placePos)) {
                        final Anchor a = new Anchor(AnchorState.Loaded, anchor.charges + 1, System.currentTimeMillis());
                        this.anchors.remove(this.placePos);
                        this.anchors.put(this.placePos, a);
                        break;
                    }
                    break;
                }
                case Loaded: {
                    if (this.explodeUpdate(this.placePos)) {
                        this.anchors.remove(this.placePos);
                        this.anchors.put(this.placePos, new Anchor(AnchorState.Air, 0, System.currentTimeMillis()));
                        break;
                    }
                    break;
                }
                case Air: {
                    if (this.timer <= 1.0 / this.speed.get()) {
                        return;
                    }
                    if (this.placeUpdate()) {
                        this.anchors.remove(this.placePos);
                        this.anchors.put(this.placePos, new Anchor(AnchorState.Anchor, 0, System.currentTimeMillis()));
                        this.timer = 0.0;
                        break;
                    }
                    break;
                }
            }
        }
        else {
            switch (anchor.state) {
                case Air: {
                    if (this.placeUpdate()) {
                        this.anchors.remove(this.placePos);
                        this.anchors.put(this.placePos, new Anchor(AnchorState.Anchor, 0, System.currentTimeMillis()));
                        break;
                    }
                    break;
                }
                case Anchor: {
                    if (this.chargeUpdate(this.placePos)) {
                        final Anchor a = new Anchor(AnchorState.Loaded, anchor.charges + 1, System.currentTimeMillis());
                        this.anchors.remove(this.placePos);
                        this.anchors.put(this.placePos, a);
                        break;
                    }
                    break;
                }
                case Loaded: {
                    if (this.timer <= 1.0 / this.speed.get()) {
                        return;
                    }
                    if (this.explodeUpdate(this.placePos)) {
                        this.anchors.remove(this.placePos);
                        this.anchors.put(this.placePos, new Anchor(AnchorState.Air, 0, System.currentTimeMillis()));
                        this.timer = 0.0;
                        break;
                    }
                    break;
                }
            }
        }
    }

    private void place(final Hand hand) {
        this.placeBlock(hand, this.placeData.pos().toCenterPos(), this.placeData.dir(), this.placeData.pos());
        if (this.placeSwing.get()) {
            this.clientSwing(this.placeHand.get(), hand);
        }
    }

    private Anchor getAnchor(final BlockPos pos) {
        if (this.anchors.containsKey(pos)) {
            return this.anchors.get(pos);
        }
        final BlockState state = this.mc.world.getBlockState(pos);
        return new Anchor((state.getBlock() == Blocks.RESPAWN_ANCHOR) ? (((int)state.get((Property)Properties.CHARGES) < 1) ? AnchorState.Anchor : AnchorState.Loaded) : AnchorState.Air, (state.getBlock() == Blocks.RESPAWN_ANCHOR) ? ((int)state.get((Property)Properties.CHARGES)) : 0, System.currentTimeMillis());
    }

    private boolean placeUpdate() {
        final Hand hand = Managers.HOLDING.isHolding(Items.RESPAWN_ANCHOR) ? Hand.MAIN_HAND : ((this.mc.player.getOffHandStack().getItem() == Items.RESPAWN_ANCHOR) ? Hand.OFF_HAND : null);
        boolean switched = hand != null;
        if (!switched) {
            switch (this.switchMode.get()) {
                case Silent:
                case Normal: {
                    final FindItemResult result = InvUtils.findInHotbar(Items.RESPAWN_ANCHOR);
                    switched = result.found();
                    break;
                }
                case PickSilent:
                case InvSwitch: {
                    final FindItemResult result = InvUtils.find(Items.RESPAWN_ANCHOR);
                    switched = result.found();
                    break;
                }
            }
        }
        if (!switched) {
            return false;
        }
        if (SettingUtils.shouldRotate(RotationType.BlockPlace) && !Managers.ROTATION.start(this.placeData.pos(), this.priority, RotationType.BlockPlace, Objects.hash(this.name + "placing"))) {
            return false;
        }
        if (hand == null) {
            switch (this.switchMode.get()) {
                case Silent:
                case Normal: {
                    final FindItemResult result = InvUtils.findInHotbar(Items.RESPAWN_ANCHOR);
                    InvUtils.swap(result.slot(), true);
                    break;
                }
                case PickSilent: {
                    final FindItemResult result = InvUtils.find(Items.RESPAWN_ANCHOR);
                    switched = BOInvUtils.pickSwitch(result.slot());
                    break;
                }
                case InvSwitch: {
                    final FindItemResult result = InvUtils.find(Items.RESPAWN_ANCHOR);
                    switched = BOInvUtils.invSwitch(result.slot());
                    break;
                }
            }
        }
        if (!switched) {
            return false;
        }
        this.place((hand == null) ? Hand.MAIN_HAND : hand);
        if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
            Managers.ROTATION.end(Objects.hash(this.name + "placing"));
        }
        if (hand == null) {
            switch (this.switchMode.get()) {
                case Silent: {
                    InvUtils.swapBack();
                    break;
                }
                case PickSilent: {
                    BOInvUtils.pickSwapBack();
                    break;
                }
                case InvSwitch: {
                    BOInvUtils.swapBack();
                    break;
                }
            }
        }
        return true;
    }

    private boolean chargeUpdate(final BlockPos pos) {
        final Hand hand = Managers.HOLDING.isHolding(Items.GLOWSTONE) ? Hand.MAIN_HAND : ((this.mc.player.getOffHandStack().getItem() == Items.GLOWSTONE) ? Hand.OFF_HAND : null);
        final Direction dir = SettingUtils.getPlaceOnDirection(pos);
        if (dir == null) {
            return false;
        }
        boolean switched = hand != null;
        if (!switched) {
            switch (this.switchMode.get()) {
                case Silent:
                case Normal: {
                    final FindItemResult result = InvUtils.findInHotbar(Items.GLOWSTONE);
                    switched = result.found();
                    break;
                }
                case PickSilent:
                case InvSwitch: {
                    final FindItemResult result = InvUtils.find(Items.GLOWSTONE);
                    switched = result.found();
                    break;
                }
            }
        }
        if (!switched) {
            return false;
        }
        if (SettingUtils.shouldRotate(RotationType.Interact) && !Managers.ROTATION.start(pos, this.priority, RotationType.Interact, Objects.hash(this.name + "interact"))) {
            return false;
        }
        if (hand == null) {
            switch (this.switchMode.get()) {
                case Silent:
                case Normal: {
                    final FindItemResult result = InvUtils.findInHotbar(Items.GLOWSTONE);
                    InvUtils.swap(result.slot(), true);
                    break;
                }
                case PickSilent: {
                    final FindItemResult result = InvUtils.find(Items.GLOWSTONE);
                    switched = BOInvUtils.pickSwitch(result.slot());
                    break;
                }
                case InvSwitch: {
                    final FindItemResult result = InvUtils.find(Items.GLOWSTONE);
                    switched = BOInvUtils.invSwitch(result.slot());
                    break;
                }
            }
        }
        if (!switched) {
            return false;
        }
        this.interact(pos, dir, (hand == null) ? Hand.MAIN_HAND : hand);
        if (SettingUtils.shouldRotate(RotationType.Interact)) {
            Managers.ROTATION.end(Objects.hash(this.name + "interact"));
        }
        if (hand == null) {
            switch (this.switchMode.get()) {
                case Silent: {
                    InvUtils.swapBack();
                    break;
                }
                case PickSilent: {
                    BOInvUtils.pickSwapBack();
                    break;
                }
                case InvSwitch: {
                    BOInvUtils.swapBack();
                    break;
                }
            }
        }
        return true;
    }

    private boolean explodeUpdate(final BlockPos pos) {
        final Hand hand = Managers.HOLDING.isHolding(Items.GLOWSTONE) ? ((this.mc.player.getOffHandStack().getItem() != Items.GLOWSTONE) ? Hand.OFF_HAND : null) : Hand.MAIN_HAND;
        final Direction dir = SettingUtils.getPlaceOnDirection(pos);
        if (dir == null) {
            return false;
        }
        boolean switched = hand != null;
        if (!switched) {
            switch (this.switchMode.get()) {
                case Silent:
                case Normal: {
                    final FindItemResult result = InvUtils.findInHotbar(stack -> stack.getItem() != Items.GLOWSTONE);
                    switched = result.found();
                    break;
                }
                case PickSilent:
                case InvSwitch: {
                    final FindItemResult result = InvUtils.find(stack -> stack.getItem() != Items.GLOWSTONE);
                    switched = result.found();
                    break;
                }
            }
        }
        if (!switched) {
            return false;
        }
        if (SettingUtils.shouldRotate(RotationType.Interact) && !Managers.ROTATION.start(pos, this.priority, RotationType.Interact, Objects.hash(this.name + "explode"))) {
            return false;
        }
        if (hand == null) {
            switch (this.switchMode.get()) {
                case Silent:
                case Normal: {
                    final FindItemResult result = InvUtils.findInHotbar(item -> item.getItem() != Items.GLOWSTONE);
                    InvUtils.swap(result.slot(), true);
                    break;
                }
                case PickSilent: {
                    final FindItemResult result = InvUtils.find(item -> item.getItem() != Items.GLOWSTONE);
                    switched = BOInvUtils.pickSwitch(result.slot());
                    break;
                }
                case InvSwitch: {
                    final FindItemResult result = InvUtils.find(item -> item.getItem() != Items.GLOWSTONE);
                    switched = BOInvUtils.invSwitch(result.slot());
                    break;
                }
            }
        }
        if (!switched) {
            return false;
        }
        this.interact(pos, dir, (hand == null) ? Hand.MAIN_HAND : hand);
        if (SettingUtils.shouldRotate(RotationType.Interact)) {
            Managers.ROTATION.end(Objects.hash(this.name + "explode"));
        }
        if (hand == null) {
            switch (this.switchMode.get()) {
                case Silent: {
                    InvUtils.swapBack();
                    break;
                }
                case PickSilent: {
                    BOInvUtils.pickSwapBack();
                    break;
                }
                case InvSwitch: {
                    BOInvUtils.swapBack();
                    break;
                }
            }
        }
        return true;
    }

    private void interact(final BlockPos pos, final Direction dir, final Hand hand) {
        this.interactBlock(hand, pos.toCenterPos(), dir, pos);
        if (this.interactSwing.get()) {
            this.clientSwing(this.interactHand.get(), hand);
        }
    }

    private boolean dmgCheck(final double dmg, final double self) {
        return dmg >= this.bestDmg && dmg >= this.minDmg.get() && self <= this.maxDmg.get() && dmg / self >= this.minRatio.get();
    }

    private double getDmg(final BlockPos pos) {
        double highest = -1.0;
        for (final PlayerEntity target : this.targets) {
            highest = Math.max(highest, BODamageUtils.anchorDamage(target, target.getBoundingBox().offset(CrystalUtil.getMotionVec(target, this.Predict.get(), true)), pos));
        }
        return highest;
    }

    private Vec3d calcPredict(final Entity e, final int ticks) {
        if (ticks == 0) {
            return e.getPos();
        }
        return new Vec3d(e.getX() + (e.getX() - e.lastRenderX) * ticks, e.getY() + (e.getY() - e.lastRenderY) * ticks, e.getZ() + (e.getZ() - e.lastRenderZ) * ticks);
    }

    private Vec3d smoothMove(final Vec3d current, final Vec3d target, final double delta) {
        if (current == null) {
            return target;
        }
        final double absX = Math.abs(current.x - target.x);
        final double absY = Math.abs(current.y - target.y);
        final double absZ = Math.abs(current.z - target.z);
        final double x = (absX + Math.pow(absX, this.animationMoveExponent.get() - 1.0)) * delta;
        final double y = (absX + Math.pow(absY, this.animationMoveExponent.get() - 1.0)) * delta;
        final double z = (absX + Math.pow(absZ, this.animationMoveExponent.get() - 1.0)) * delta;
        return new Vec3d((current.x > target.x) ? Math.max(target.x, current.x - x) : Math.min(target.x, current.x + x), (current.y > target.y) ? Math.max(target.y, current.y - y) : Math.min(target.y, current.y + y), (current.z > target.z) ? Math.max(target.z, current.z - z) : Math.min(target.z, current.z + z));
    }

    public enum LogicMode
    {
        PlaceBreak("PlaceBreak", 0),
        BreakPlace("BreakPlace", 1);

        LogicMode(final String string, final int i) {
        }
    }

    public enum SwitchMode
    {
        Silent("Silent", 0),
        Normal("Normal", 1),
        PickSilent("PickSilent", 2),
        InvSwitch("InvSwitch", 3),
        Disabled("Disabled", 4);

        SwitchMode(final String string, final int i) {
        }
    }

    public enum AnchorState
    {
        Air("Air", 0),
        Anchor("Anchor", 1),
        Loaded("Loaded", 2);

        AnchorState(final String string, final int i) {
        }
    }

    record Anchor(AnchorState state, int charges, long time) {}

    public enum FadeMode
    {
        Up("Up", 0),
        Down("Down", 1),
        Normal("Normal", 2),
        Test("Test", 3);

        FadeMode(final String string, final int i) {
        }
    }
}
