package kassuk.addon.aurora.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import kassuk.addon.aurora.mixins.IPostEffectProcessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.JsonEffectShaderProgram;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.Uniform;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PostShader
{
    protected final MinecraftClient mc;
    protected PostEffectProcessor shader;
    public Consumer<PostShader> initCallback;
    private final Identifier location;

    public PostShader(final Identifier id, final Consumer<PostShader> initCallback) {
        this.mc = MinecraftClient.getInstance();
        this.initCallback = initCallback;
        this.location = id;
        this.initShader();
    }

    public ShaderUniform set(final String name) {
        return this.findUniform(name);
    }

    public void render(final float tickDelta) {
        final PostEffectProcessor sg = this.getShader();
        if (sg != null) {
            RenderSystem.disableBlend();
            RenderSystem.disableDepthTest();
            RenderSystem.resetTextureMatrix();
            sg.render(tickDelta);
            MinecraftClient.getInstance().getFramebuffer().beginWrite(true);
            RenderSystem.disableBlend();
            RenderSystem.blendFunc(770, 771);
            RenderSystem.enableDepthTest();
        }
    }

    protected ShaderUniform findUniform(final String name) {
        if (this.shader == null) {
            this.initShader();
        }
        final List<Uniform> uniforms = new ArrayList<Uniform>();
        for (final PostEffectPass pass : ((IPostEffectProcessor)this.shader).getPasses()) {
            final JsonEffectShaderProgram program = pass.getProgram();
            uniforms.add(program.getUniformByNameOrDummy(name));
        }
        return new ShaderUniform(uniforms);
    }

    public PostEffectProcessor getShader() {
        if (this.shader == null) {
            this.initShader();
        }
        return this.shader;
    }

    protected PostEffectProcessor parseShader(final MinecraftClient mc, final Identifier location) throws IOException {
        return new PostEffectProcessor(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), location);
    }

    private void initShader() {
        try {
            (this.shader = this.parseShader(this.mc, this.location)).setupDimensions(this.mc.getWindow().getFramebufferWidth(), this.mc.getWindow().getFramebufferHeight());
            if (this.initCallback != null) {
                this.initCallback.accept(this);
            }
        }
        catch (final IOException e) {
            throw new RuntimeException("Failed to initialized post shader program", e);
        }
    }

    public void set(final String name, final int value) {
        this.set(name).set(value);
    }

    public void set(final String name, final float value) {
        this.set(name).set(value);
    }

    public void set(final String name, final float v0, final float v1) {
        this.set(name).set(v0, v1);
    }

    public void set(final String name, final float v0, final float v1, final float v2, final float v3) {
        this.set(name).set(v0, v1, v2, v3);
    }

    public void set(final String name, final float v0, final float v1, final float v2) {
        this.set(name).set(v0, v1, v2);
    }
}
