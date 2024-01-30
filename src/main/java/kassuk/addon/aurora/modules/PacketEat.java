package kassuk.addon.aurora.modules;

import java.util.Objects;
import kassuk.addon.aurora.BlackOut;
import kassuk.addon.aurora.BlackOutModule;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Item;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;

public class PacketEat
    extends BlackOutModule {
    private Item PackEatItem;

    public PacketEat() {
        super(BlackOut.BLACKOUT, "PackEat", "PackEat");
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (this.mc.player != null && this.mc.player.isUsingItem()) {
            this.PackEatItem = this.mc.player.getActiveItem().getItem();
        }
    }

    @EventHandler
    public void onPacket(PacketEvent.Send event) {
        try {
            PlayerActionC2SPacket packet;
            Packet packet2 = event.packet;
            if (packet2 instanceof PlayerActionC2SPacket && (packet = (PlayerActionC2SPacket)packet2).getAction() == PlayerActionC2SPacket.Action.RELEASE_USE_ITEM && Objects.requireNonNull(this.PackEatItem.getFoodComponent()).isAlwaysEdible()) {
                event.cancel();
            }
        }
        catch (Exception exception) {
        }
    }
}

