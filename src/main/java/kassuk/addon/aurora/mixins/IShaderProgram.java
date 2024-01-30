package kassuk.addon.aurora.mixins;

import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.gl.GlUniform;
import java.util.Map;
import net.minecraft.client.gl.ShaderProgram;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ ShaderProgram.class })
public interface IShaderProgram
{
    @Accessor("loadedUniforms")
    Map<String, GlUniform> getUniformsHook();
}
