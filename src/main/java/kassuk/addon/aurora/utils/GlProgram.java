package kassuk.addon.aurora.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import kassuk.addon.aurora.mixins.IShaderProgram;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Pair;

public class GlProgram {
    protected final MinecraftClient mc = MinecraftClient.getInstance();
    private static final List<Pair<Function<ResourceFactory, ShaderProgram>, Consumer<ShaderProgram>>> REGISTERED_PROGRAMS = new ArrayList<>();
    public ShaderProgram backingProgram;

    public GlProgram(String id, VertexFormat vertexFormat) {
        REGISTERED_PROGRAMS.add(new Pair((Function)resourceFactory -> {
            try {
                return new OwoShaderProgram((ResourceFactory) resourceFactory, id, vertexFormat);
            } catch (IOException var4) {
                throw new RuntimeException("Failed to initialized shader program", var4);
            }
        }, (Consumer)program -> {
            this.backingProgram = (ShaderProgram) program;
            this.setup();
        }));
    }


    protected GlUniform findUniform(String name) {
        return ((IShaderProgram)this.backingProgram).getUniformsHook().get(name);
    }

    public void use() {
        RenderSystem.setShader(() -> this.backingProgram);
    }

    protected void setup() {
    }

    public static void forEachProgram(Consumer<Pair<Function<ResourceFactory, ShaderProgram>, Consumer<ShaderProgram>>> loader) {
        REGISTERED_PROGRAMS.forEach(loader);
    }

    public static class OwoShaderProgram
        extends ShaderProgram {
        private OwoShaderProgram(ResourceFactory factory, String name, VertexFormat format) throws IOException {
            super(factory, name, format);
        }
    }
}

