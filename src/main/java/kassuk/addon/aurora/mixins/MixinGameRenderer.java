package kassuk.addon.aurora.mixins;

import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import kassuk.addon.aurora.utils.GlProgram;
import java.util.function.Function;
import kassuk.addon.aurora.utils.RenderUtils;
import java.util.function.Consumer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.util.Pair;
import net.minecraft.client.gl.ShaderStage;
import java.util.List;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ GameRenderer.class })
public abstract class MixinGameRenderer
{
    @Inject(method = { "loadPrograms" }, at = { @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0) }, locals = LocalCapture.CAPTURE_FAILHARD)
    void loadAllTheShaders(final ResourceFactory factory, final CallbackInfo ci, final List<ShaderStage> stages, final List<Pair<ShaderProgram, Consumer<ShaderProgram>>> shadersToLoad) {
        RenderUtils.initShaders();
        GlProgram.forEachProgram(loader -> shadersToLoad.add(new Pair(((Function)loader.getLeft()).apply(factory), loader.getRight())));
    }
}
