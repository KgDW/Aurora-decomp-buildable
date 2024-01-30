package kassuk.addon.aurora.mixins;

import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.gl.PostEffectPass;
import java.util.List;
import net.minecraft.client.gl.PostEffectProcessor;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ PostEffectProcessor.class })
public interface IPostEffectProcessor
{
    @Accessor("passes")
    List<PostEffectPass> getPasses();
}
