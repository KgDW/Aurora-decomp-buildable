package kassuk.addon.aurora.mixins;

import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ EntityS2CPacket.class })
public interface IEntityS2CPacket
{
    @Accessor("id")
    int getId();
}
