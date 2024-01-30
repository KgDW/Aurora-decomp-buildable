package kassuk.addon.aurora.modules;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.render.entity.PlayerModelPart;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import kassuk.addon.aurora.BlackOut;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;

public class SkinBlinker extends Module
{
    private final SettingGroup sgGeneral;
    private final Setting<Mode> mode;
    private final Setting<SequentialMode> seqMode;
    private final Setting<Integer> sequentialDelay;
    private final Setting<Boolean> cape;
    private final Setting<Integer> capeDelay;
    private final Setting<Boolean> head;
    private final Setting<Integer> headDelay;
    private final Setting<Boolean> jacket;
    private final Setting<Integer> jacketDelay;
    private final Setting<Boolean> leftArm;
    private final Setting<Integer> leftArmDelay;
    private final Setting<Boolean> rightArm;
    private final Setting<Integer> rightArmDelay;
    private final Setting<Boolean> leftLeg;
    private final Setting<Integer> leftLegDelay;
    private final Setting<Boolean> rightLeg;
    private final Setting<Integer> rightLegDelay;
    private int ticksPassed;
    private int headTimer;
    private int jacketTimer;
    private int leftArmTimer;
    private int rightArmTimer;
    private int leftLegTimer;
    private int rightLegTimer;
    private int capeTimer;

    public SkinBlinker() {
        super(BlackOut.BLACKOUT, "Skin Blinker", "Blinks different parts of your skin.");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.mode = (Setting<Mode>)this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)new EnumSetting.Builder().name("mode")).description("What mode the skin blinker should behave in.")).defaultValue(Mode.Sequential)).build());
        this.seqMode = (Setting<SequentialMode>)this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)new EnumSetting.Builder().name("sequential-mode")).description("Whether to toggle your skin parts on or off.")).defaultValue(SequentialMode.On)).visible(() -> this.mode.get() == Mode.Sequential)).build());
        this.sequentialDelay = (Setting<Integer>)this.sgGeneral.add((Setting) new IntSetting.Builder().name("sequential-delay").description("Delay in ticks between each part of skin to toggle.").defaultValue(5).min(1).sliderRange(1, 15).visible(() -> this.mode.get() == Mode.Sequential).build());
        this.cape = (Setting<Boolean>)this.sgGeneral.add((Setting) new BoolSetting.Builder().name("cape").description("Blinks the cape part of your skin (only works if you have a Mojang cape).").defaultValue(true).visible(() -> this.mode.get() == Mode.Individual).build());
        this.capeDelay = (Setting<Integer>)this.sgGeneral.add((Setting) new IntSetting.Builder().name("cape-delay").description("Delay in ticks between toggling the cape part of the skin.").defaultValue(10).min(1).sliderRange(1, 15).visible(() -> this.mode.get() == Mode.Individual && this.cape.get()).build());
        this.head = (Setting<Boolean>)this.sgGeneral.add((Setting) new BoolSetting.Builder().name("head").description("Blinks the head part of your skin.").defaultValue(true).visible(() -> this.mode.get() == Mode.Individual).build());
        this.headDelay = (Setting<Integer>)this.sgGeneral.add((Setting) new IntSetting.Builder().name("head-delay").description("Delay in ticks between toggling the head part of the skin.").defaultValue(10).min(1).sliderRange(1, 15).visible(() -> this.mode.get() == Mode.Individual && this.head.get()).build());
        this.jacket = (Setting<Boolean>)this.sgGeneral.add((Setting) new BoolSetting.Builder().name("jacket").description("Blinks the torso part of your skin.").defaultValue(true).visible(() -> this.mode.get() == Mode.Individual).build());
        this.jacketDelay = (Setting<Integer>)this.sgGeneral.add((Setting) new IntSetting.Builder().name("jacket-delay").description("Delay in ticks between toggling the jacket part of the skin.").defaultValue(10).min(1).sliderRange(1, 15).visible(() -> this.mode.get() == Mode.Individual && this.jacket.get()).build());
        this.leftArm = (Setting<Boolean>)this.sgGeneral.add((Setting) new BoolSetting.Builder().name("left-arm").description("Blinks the left arm of your skin.").defaultValue(true).visible(() -> this.mode.get() == Mode.Individual).build());
        this.leftArmDelay = (Setting<Integer>)this.sgGeneral.add((Setting) new IntSetting.Builder().name("left-arm-delay").description("Delay in ticks between toggling the left arm part of the skin.").defaultValue(10).min(1).sliderRange(1, 15).visible(() -> this.mode.get() == Mode.Individual && this.leftArm.get()).build());
        this.rightArm = (Setting<Boolean>)this.sgGeneral.add((Setting) new BoolSetting.Builder().name("right-arm").description("Blinks the right arm of your skin.").defaultValue(true).visible(() -> this.mode.get() == Mode.Individual).build());
        this.rightArmDelay = (Setting<Integer>)this.sgGeneral.add((Setting) new IntSetting.Builder().name("right-arm-delay").description("Delay in ticks between toggling the right arm part of the skin.").defaultValue(10).min(1).sliderRange(1, 15).visible(() -> this.mode.get() == Mode.Individual && this.rightArm.get()).build());
        this.leftLeg = (Setting<Boolean>)this.sgGeneral.add((Setting) new BoolSetting.Builder().name("left-leg").description("Blinks the head left leg of your skin.").defaultValue(true).visible(() -> this.mode.get() == Mode.Individual).build());
        this.leftLegDelay = (Setting<Integer>)this.sgGeneral.add((Setting) new IntSetting.Builder().name("left-leg-delay").description("Delay in ticks between toggling the left leg part of the skin.").defaultValue(10).min(1).sliderRange(1, 15).visible(() -> this.mode.get() == Mode.Individual && this.leftLeg.get()).build());
        this.rightLeg = (Setting<Boolean>)this.sgGeneral.add((Setting) new BoolSetting.Builder().name("right-leg").description("Blinks the head right leg of your skin.").defaultValue(true).visible(() -> this.mode.get() == Mode.Individual).build());
        this.rightLegDelay = (Setting<Integer>)this.sgGeneral.add((Setting) new IntSetting.Builder().name("right-leg-delay").description("Delay in ticks between toggling the right leg part of the skin.").defaultValue(10).min(1).sliderRange(1, 15).visible(() -> this.mode.get() == Mode.Individual && this.rightLeg.get()).build());
    }

    public void onActivate() {
        this.ticksPassed = 0;
        this.headTimer = 0;
        this.jacketTimer = 0;
        this.capeTimer = 0;
        this.leftArmTimer = 0;
        this.rightArmTimer = 0;
        this.leftLegTimer = 0;
        this.rightLegTimer = 0;
    }

    @EventHandler
    private void onTick(final TickEvent.Post event) {
        if (this.mode.get() == Mode.Sequential) {
            if (this.ticksPassed < this.sequentialDelay.get() * 5) {
                ++this.ticksPassed;
            }
            else {
                this.ticksPassed = 0;
            }
            if (this.ticksPassed > 0) {
                this.mc.options.togglePlayerModelPart(PlayerModelPart.HAT, this.hat());
            }
            if (this.ticksPassed > this.sequentialDelay.get()) {
                this.mc.options.togglePlayerModelPart(PlayerModelPart.LEFT_SLEEVE, this.arm());
                this.mc.options.togglePlayerModelPart(PlayerModelPart.RIGHT_SLEEVE, this.arm());
            }
            if (this.ticksPassed > this.sequentialDelay.get() * 2) {
                this.mc.options.togglePlayerModelPart(PlayerModelPart.JACKET, this.mid());
            }
            if (this.ticksPassed > this.sequentialDelay.get() * 3) {
                this.mc.options.togglePlayerModelPart(PlayerModelPart.LEFT_PANTS_LEG, this.legs());
                this.mc.options.togglePlayerModelPart(PlayerModelPart.RIGHT_PANTS_LEG, this.legs());
            }
        }
        else {
            if (this.cape.get()) {
                if (this.capeTimer < this.capeDelay.get() * 2) {
                    ++this.capeTimer;
                    this.mc.options.togglePlayerModelPart(PlayerModelPart.CAPE, this.capeTimer <= this.capeDelay.get());
                }
                else {
                    this.capeTimer = 0;
                }
            }
            if (this.head.get()) {
                if (this.headTimer < this.headDelay.get() * 2) {
                    ++this.headTimer;
                    this.mc.options.togglePlayerModelPart(PlayerModelPart.HAT, this.headTimer <= this.headDelay.get());
                }
                else {
                    this.headTimer = 0;
                }
            }
            if (this.jacket.get()) {
                if (this.jacketTimer < this.jacketDelay.get() * 2) {
                    ++this.jacketTimer;
                    this.mc.options.togglePlayerModelPart(PlayerModelPart.JACKET, this.jacketTimer <= this.jacketDelay.get());
                }
                else {
                    this.jacketTimer = 0;
                }
            }
            if (this.leftArm.get()) {
                if (this.leftArmTimer < this.leftArmDelay.get() * 2) {
                    ++this.leftArmTimer;
                    this.mc.options.togglePlayerModelPart(PlayerModelPart.LEFT_SLEEVE, this.leftArmTimer <= this.leftArmDelay.get());
                }
                else {
                    this.leftArmTimer = 0;
                }
            }
            if (this.rightArm.get()) {
                if (this.rightArmTimer < this.rightArmDelay.get() * 2) {
                    ++this.rightArmTimer;
                    this.mc.options.togglePlayerModelPart(PlayerModelPart.RIGHT_SLEEVE, this.rightArmTimer <= this.rightArmDelay.get());
                }
                else {
                    this.rightArmTimer = 0;
                }
            }
            if (this.leftLeg.get()) {
                if (this.leftLegTimer < this.leftLegDelay.get() * 2) {
                    ++this.leftLegTimer;
                    this.mc.options.togglePlayerModelPart(PlayerModelPart.LEFT_PANTS_LEG, this.leftLegTimer <= this.leftLegDelay.get());
                }
                else {
                    this.leftLegTimer = 0;
                }
            }
            if (this.rightLeg.get()) {
                if (this.rightLegTimer < this.rightLegDelay.get() * 2) {
                    ++this.rightLegTimer;
                    this.mc.options.togglePlayerModelPart(PlayerModelPart.RIGHT_PANTS_LEG, this.rightLegTimer <= this.rightLegDelay.get());
                }
                else {
                    this.rightLegTimer = 0;
                }
            }
        }
    }

    private boolean hat() {
        if (this.seqMode.get() == SequentialMode.Off) {
            return this.ticksPassed <= this.sequentialDelay.get();
        }
        return this.ticksPassed > this.sequentialDelay.get();
    }

    private boolean arm() {
        if (this.seqMode.get() == SequentialMode.Off) {
            return this.ticksPassed <= this.sequentialDelay.get() * 2;
        }
        return this.ticksPassed > this.sequentialDelay.get() * 2;
    }

    private boolean mid() {
        if (this.seqMode.get() == SequentialMode.Off) {
            return this.ticksPassed <= this.sequentialDelay.get() * 3;
        }
        return this.ticksPassed > this.sequentialDelay.get() * 3;
    }

    private boolean legs() {
        if (this.seqMode.get() == SequentialMode.Off) {
            return this.ticksPassed <= this.sequentialDelay.get() * 4;
        }
        return this.ticksPassed > this.sequentialDelay.get() * 4;
    }

    public enum Mode
    {
        Sequential("Sequential", 0),
        Individual("Individual", 1);

        Mode(final String string, final int i) {
        }
    }

    public enum SequentialMode
    {
        On("On", 0),
        Off("Off", 1);

        SequentialMode(final String string, final int i) {
        }
    }
}
