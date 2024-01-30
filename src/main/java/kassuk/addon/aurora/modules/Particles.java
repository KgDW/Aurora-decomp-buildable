package kassuk.addon.aurora.modules;

import org.joml.Matrix4f;
import java.util.Iterator;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.client.util.math.MatrixStack;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.Tessellator;
import kassuk.addon.aurora.utils.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.orbit.EventHandler;
import kassuk.addon.aurora.utils.MathUtils;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import java.util.Objects;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import kassuk.addon.aurora.BlackOut;
import java.util.ArrayList;
import net.minecraft.util.Identifier;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import kassuk.addon.aurora.BlackOutModule;

public class Particles extends BlackOutModule
{
    private final SettingGroup sgFireFiles;
    private final SettingGroup sgGeneral;
    private final Setting<Boolean> fireFilesSet;
    private final Setting<Integer> ffcount;
    private final Setting<Double> ffsize;
    private final Setting<Mode> mode;
    private final Setting<Integer> count;
    private final Setting<Double> size;
    private final Setting<SettingColor> color;
    private final Identifier star;
    private final Identifier snowflake;
    private final Identifier vanillaSnowflake;
    private final Identifier firefly;
    private final ArrayList<ParticleBase> fireFlies;
    private final ArrayList<ParticleBase> particles;

    public Particles() {
        super(BlackOut.BLACKOUT, "Particles", "Render some particles to make your game look better.");
        this.sgFireFiles = this.settings.createGroup("FireFlies");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.fireFilesSet = (Setting<Boolean>)this.sgFireFiles.add((Setting) new BoolSetting.Builder().name("FireFiles").defaultValue(true).build());
        final SettingGroup sgFireFiles = this.sgFireFiles;
        final IntSetting.Builder max = new IntSetting.Builder().name("FFCount").defaultValue(30).min(20).max(200);
        final Setting<Boolean> fireFilesSet = this.fireFilesSet;
        Objects.requireNonNull(fireFilesSet);
        this.ffcount = (Setting<Integer>)sgFireFiles.add((Setting) max.visible(fireFilesSet::get).build());
        final SettingGroup sgFireFiles2 = this.sgFireFiles;
        final DoubleSetting.Builder max2 = new DoubleSetting.Builder().name("FFSize").defaultValue(1.0).min(0.1).max(2.0);
        final Setting<Boolean> fireFilesSet2 = this.fireFilesSet;
        Objects.requireNonNull(fireFilesSet2);
        this.ffsize = (Setting<Double>)sgFireFiles2.add((Setting) max2.visible(fireFilesSet2::get).build());
        this.mode = (Setting<Mode>)this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)new EnumSetting.Builder().name("Mode")).defaultValue(Mode.Snowflake)).build());
        this.count = (Setting<Integer>)this.sgGeneral.add((Setting) new IntSetting.Builder().name("Count").min(20).max(8000).defaultValue(100).build());
        this.size = (Setting<Double>)this.sgGeneral.add((Setting) new DoubleSetting.Builder().name("Size").defaultValue(1.0).min(0.1).max(6.0).build());
        this.color = (Setting<SettingColor>)this.sgGeneral.add((Setting) new ColorSetting.Builder().name("Color").defaultValue(new SettingColor(255, 255, 255)).build());
        this.star = new Identifier("aurora", "textures/star.png");
        this.snowflake = new Identifier("aurora", "textures/snowflake.png");
        this.vanillaSnowflake = new Identifier("textures/environment/snow.png");
        this.firefly = new Identifier("aurora", "textures/firefly.png");
        this.fireFlies = new ArrayList<ParticleBase>();
        this.particles = new ArrayList<ParticleBase>();
    }

    @EventHandler
    public void onTick(final TickEvent.Pre event) {
        this.fireFlies.removeIf(ParticleBase::tick);
        this.particles.removeIf(ParticleBase::tick);
        for (int i = this.fireFlies.size(); i < this.ffcount.get(); ++i) {
            if (this.fireFilesSet.get()) {
                this.fireFlies.add(new FireFly((float)(this.mc.player.getX() + MathUtils.random(-25.0f, 25.0f)), (float)(this.mc.player.getY() + MathUtils.random(2.0f, 15.0f)), (float)(this.mc.player.getZ() + MathUtils.random(-25.0f, 25.0f)), MathUtils.random(-0.2f, 0.2f), MathUtils.random(-0.1f, 0.1f), MathUtils.random(-0.2f, 0.2f)));
            }
        }
        for (int j = this.particles.size(); j < this.count.get(); ++j) {
            if (this.mode.get() != Mode.Off) {
                this.particles.add(new ParticleBase((float)(this.mc.player.getX() + MathUtils.random(-48.0f, 48.0f)), (float)(this.mc.player.getY() + MathUtils.random(2.0f, 48.0f)), (float)(this.mc.player.getZ() + MathUtils.random(-48.0f, 48.0f)), MathUtils.random(-0.4f, 0.4f), MathUtils.random(-0.1f, 0.1f), MathUtils.random(-0.4f, 0.4f)));
            }
        }
    }

    @EventHandler
    public void onRender(final Render3DEvent event) {
        if (this.fireFilesSet.get()) {
            event.matrices.push();
            RenderSystem.setShaderTexture(0, this.firefly);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.setShader(() -> RenderUtils.TEXTURE_COLOR_PROGRAM.backingProgram);
            final BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            this.fireFlies.forEach(p -> p.render(bufferBuilder));
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
            RenderSystem.depthMask(true);
            RenderSystem.disableDepthTest();
            RenderSystem.disableBlend();
            event.matrices.pop();
        }
        if (this.mode.get() != Mode.Off) {
            event.matrices.push();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.setShader(() -> RenderUtils.TEXTURE_COLOR_PROGRAM.backingProgram);
            final BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            this.particles.forEach(p -> p.render(bufferBuilder));
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
            RenderSystem.depthMask(true);
            RenderSystem.disableDepthTest();
            RenderSystem.disableBlend();
            event.matrices.pop();
        }
    }

    public enum Mode
    {
        Off("Off", 0),
        Star("Star", 1),
        Snowflake("Snowflake", 2),
        VanillaSnowflake("VanillaSnowflake", 3),
        Firefly("Firefly", 4);

        Mode(final String string, final int i) {
        }
    }

    public class FireFly extends ParticleBase
    {
        private final List<Trail> trails;

        public FireFly(final float posX, final float posY, final float posZ, final float motionX, final float motionY, final float motionZ) {
            super(posX, posY, posZ, motionX, motionY, motionZ);
            this.trails = new ArrayList<Trail>();
        }

        @Override
        public boolean tick() {
            if (Particles.this.mc.player.squaredDistanceTo(this.posX, this.posY, this.posZ) > 100.0) {
                this.age -= 4;
            }
            else if (!Particles.this.mc.world.getBlockState(new BlockPos((int)this.posX, (int)this.posY, (int)this.posZ)).isAir()) {
                this.age -= 8;
            }
            else {
                --this.age;
            }
            if (this.age < 0) {
                return true;
            }
            this.trails.removeIf(Trail::update);
            this.prevposX = this.posX;
            this.prevposY = this.posY;
            this.prevposZ = this.posZ;
            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            this.trails.add(new Trail(new Vec3d(this.prevposX, this.prevposY, this.prevposZ), new Vec3d(this.posX, this.posY, this.posZ), Particles.this.color.get()));
            this.motionX *= 0.99f;
            this.motionY *= 0.99f;
            this.motionZ *= 0.99f;
            return false;
        }

        @Override
        public void render(final BufferBuilder bufferBuilder) {
            RenderSystem.setShaderTexture(0, Particles.this.firefly);
            if (!this.trails.isEmpty()) {
                final Camera camera = Particles.this.mc.gameRenderer.getCamera();
                for (final Trail ctx : this.trails) {
                    final Vec3d pos = ctx.interpolate(1.0f);
                    final MatrixStack matrices = new MatrixStack();
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0f));
                    matrices.translate(pos.x, pos.y, pos.z);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
                    final Matrix4f matrix = matrices.peek().getPositionMatrix();
                    final float size = Particles.this.ffsize.get().floatValue();
                    bufferBuilder.vertex(matrix, 0.0f, -size, 0.0f).texture(0.0f, 1.0f).color(RenderUtils.injectAlpha(ctx.color(), (int)(255.0f * (this.age / (float)this.maxAge) * ctx.animation(Particles.this.mc.getTickDelta()))).getPacked()).next();
                    bufferBuilder.vertex(matrix, -size, -size, 0.0f).texture(1.0f, 1.0f).color(RenderUtils.injectAlpha(ctx.color(), (int)(255.0f * (this.age / (float)this.maxAge) * ctx.animation(Particles.this.mc.getTickDelta()))).getPacked()).next();
                    bufferBuilder.vertex(matrix, -size, 0.0f, 0.0f).texture(1.0f, 0.0f).color(RenderUtils.injectAlpha(ctx.color(), (int)(255.0f * (this.age / (float)this.maxAge) * ctx.animation(Particles.this.mc.getTickDelta()))).getPacked()).next();
                    bufferBuilder.vertex(matrix, 0.0f, 0.0f, 0.0f).texture(0.0f, 0.0f).color(RenderUtils.injectAlpha(ctx.color(), (int)(255.0f * (this.age / (float)this.maxAge) * ctx.animation(Particles.this.mc.getTickDelta()))).getPacked()).next();
                }
            }
        }
    }

    private class Trail
    {
        private final Vec3d from;
        private final Vec3d to;
        private final Color color;
        private int ticks;
        private int prevTicks;

        public Trail(final Vec3d from, final Vec3d to, final Color color) {
            this.from = from;
            this.to = to;
            this.ticks = 10;
            this.color = color;
        }

        public Vec3d interpolate(final float pt) {
            final double x = this.from.x + (this.to.x - this.from.x) * pt - Particles.this.mc.getEntityRenderDispatcher().camera.getPos().getX();
            final double y = this.from.y + (this.to.y - this.from.y) * pt - Particles.this.mc.getEntityRenderDispatcher().camera.getPos().getY();
            final double z = this.from.z + (this.to.z - this.from.z) * pt - Particles.this.mc.getEntityRenderDispatcher().camera.getPos().getZ();
            return new Vec3d(x, y, z);
        }

        public double animation(final float pt) {
            return (this.prevTicks + (this.ticks - this.prevTicks) * pt) / 10.0;
        }

        public boolean update() {
            this.prevTicks = this.ticks;
            return this.ticks-- <= 0;
        }

        public Color color() {
            return this.color;
        }
    }

    public class ParticleBase
    {
        protected float prevposX;
        protected float prevposY;
        protected float prevposZ;
        protected float posX;
        protected float posY;
        protected float posZ;
        protected float motionX;
        protected float motionY;
        protected float motionZ;
        protected int age;
        protected int maxAge;

        public ParticleBase(final float posX, final float posY, final float posZ, final float motionX, final float motionY, final float motionZ) {
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            this.prevposX = posX;
            this.prevposY = posY;
            this.prevposZ = posZ;
            this.motionX = motionX;
            this.motionY = motionY;
            this.motionZ = motionZ;
            this.age = (int)MathUtils.random(100.0f, 300.0f);
            this.maxAge = this.age;
        }

        public boolean tick() {
            if (Particles.this.mc.player.squaredDistanceTo(this.posX, this.posY, this.posZ) > 4096.0) {
                this.age -= 8;
            }
            else {
                --this.age;
            }
            if (this.age < 0) {
                return true;
            }
            this.prevposX = this.posX;
            this.prevposY = this.posY;
            this.prevposZ = this.posZ;
            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            this.motionX *= 0.9f;
            this.motionY *= 0.9f;
            this.motionZ *= 0.9f;
            this.motionY -= 0.001f;
            return false;
        }

        public void render(final BufferBuilder bufferBuilder) {
            switch (Particles.this.mode.get()) {
                case Star: {
                    RenderSystem.setShaderTexture(0, Particles.this.star);
                    break;
                }
                case Snowflake: {
                    RenderSystem.setShaderTexture(0, Particles.this.snowflake);
                    break;
                }
                case VanillaSnowflake: {
                    RenderSystem.setShaderTexture(0, Particles.this.vanillaSnowflake);
                    break;
                }
                case Firefly: {
                    RenderSystem.setShaderTexture(0, Particles.this.firefly);
                    break;
                }
            }
            final MatrixStack matrices = new MatrixStack();
            final Camera camera = Particles.this.mc.gameRenderer.getCamera();
            final Vec3d pos = RenderUtils.interpolatePos(this.prevposX, this.prevposY, this.prevposZ, this.posX, this.posY, this.posZ);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0f));
            matrices.translate(pos.x, pos.y, pos.z);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
            final Matrix4f matrix1 = matrices.peek().getPositionMatrix();
            final float fSize = Particles.this.size.get().floatValue();
            bufferBuilder.vertex(matrix1, 0.0f, -fSize, 0.0f).texture(0.0f, 1.0f).color(RenderUtils.injectAlpha(Particles.this.color.get(), (int)(255.0f * (this.age / (float)this.maxAge))).getPacked()).next();
            bufferBuilder.vertex(matrix1, -fSize, -fSize, 0.0f).texture(1.0f, 1.0f).color(RenderUtils.injectAlpha(Particles.this.color.get(), (int)(255.0f * (this.age / (float)this.maxAge))).getPacked()).next();
            bufferBuilder.vertex(matrix1, -fSize, 0.0f, 0.0f).texture(1.0f, 0.0f).color(RenderUtils.injectAlpha(Particles.this.color.get(), (int)(255.0f * (this.age / (float)this.maxAge))).getPacked()).next();
            bufferBuilder.vertex(matrix1, 0.0f, 0.0f, 0.0f).texture(0.0f, 0.0f).color(RenderUtils.injectAlpha(Particles.this.color.get(), (int)(255.0f * (this.age / (float)this.maxAge))).getPacked()).next();
        }
    }
}
