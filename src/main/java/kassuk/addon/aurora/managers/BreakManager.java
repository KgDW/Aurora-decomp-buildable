package kassuk.addon.aurora.managers;

import java.util.HashMap;
import java.util.Map;
import kassuk.addon.aurora.modules.AutoMine;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.util.math.BlockPos;

public class BreakManager {
    public Map<String, BlockPos> map = new HashMap<>();

    public BreakManager() {
        MeteorClient.EVENT_BUS.subscribe(this);
    }

    public boolean isMine(BlockPos pos, boolean self) {
        for (Map.Entry<String, BlockPos> block : this.map.entrySet()) {
            if (!block.getValue().equals(pos)) continue;
            return true;
        }
        return self && Modules.get().isActive(AutoMine.class) && pos.equals(Modules.get().get(AutoMine.class).targetPos());
    }

    @EventHandler(priority=200)
    private void onReceive(PacketEvent.Receive event) {
        Packet var3 = event.packet;
        if (var3 instanceof BlockBreakingProgressS2CPacket p) {
            PlayerEntity breaker;
            Entity entity = MeteorClient.mc.world.getEntityById(p.getEntityId());
            PlayerEntity playerEntity = breaker = entity == null ? null : (PlayerEntity)entity;
            if (breaker == null) {
                return;
            }
            this.map.put(breaker.getGameProfile().getName(), p.getPos());
        }
    }
}
