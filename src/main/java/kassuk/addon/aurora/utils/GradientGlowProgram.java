package kassuk.addon.aurora.utils;

import net.minecraft.client.util.Window;
import net.minecraft.client.gl.SimpleFramebuffer;
import org.lwjgl.opengl.GL30;
import meteordevelopment.orbit.listeners.IListener;
import meteordevelopment.orbit.listeners.ConsumerListener;
import net.minecraft.client.MinecraftClient;
import meteordevelopment.meteorclient.events.game.WindowResizedEvent;
import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;

public class GradientGlowProgram extends GlProgram
{
    private GlUniform uSize;
    private GlUniform uLocation;
    private GlUniform radius;
    private GlUniform softness;
    private GlUniform color1;
    private GlUniform color2;
    private GlUniform color3;
    private GlUniform color4;
    private Framebuffer input;

    public GradientGlowProgram() {
        super("gradientglow", VertexFormats.POSITION);
        MeteorClient.EVENT_BUS.subscribe(new ConsumerListener(WindowResizedEvent.class, event -> {
            if (this.input != null) {
                this.input.resize(this.mc.getWindow().getFramebufferWidth(), this.mc.getWindow().getFramebufferHeight(), MinecraftClient.IS_SYSTEM_MAC);
            }
        }));
    }

    @Override
    public void use() {
        final Framebuffer buffer = MinecraftClient.getInstance().getFramebuffer();
        this.input.beginWrite(false);
        GL30.glBindFramebuffer(36008, buffer.fbo);
        GL30.glBlitFramebuffer(0, 0, buffer.textureWidth, buffer.textureHeight, 0, 0, buffer.textureWidth, buffer.textureHeight, 16384, 9729);
        buffer.beginWrite(false);
        super.use();
    }

    @Override
    protected void setup() {
        this.uSize = this.findUniform("uSize");
        this.uLocation = this.findUniform("uLocation");
        this.softness = this.findUniform("softness");
        this.radius = this.findUniform("radius");
        this.color1 = this.findUniform("color1");
        this.color2 = this.findUniform("color2");
        this.color3 = this.findUniform("color3");
        this.color4 = this.findUniform("color4");
        final Window window = MinecraftClient.getInstance().getWindow();
        this.input = new SimpleFramebuffer(window.getFramebufferWidth(), window.getFramebufferHeight(), false, MinecraftClient.IS_SYSTEM_MAC);
    }
}
