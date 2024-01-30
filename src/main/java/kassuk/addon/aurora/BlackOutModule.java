package kassuk.addon.aurora;

import java.util.Objects;
import kassuk.addon.aurora.enums.SwingHand;
import kassuk.addon.aurora.enums.SwingState;
import kassuk.addon.aurora.enums.SwingType;
import kassuk.addon.aurora.modules.SwingModifier;
import kassuk.addon.aurora.utils.PriorityUtils;
import kassuk.addon.aurora.utils.SettingUtils;
import kassuk.addon.aurora.utils.Util;
import meteordevelopment.meteorclient.mixininterface.IChatHud;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class BlackOutModule
    extends Module {
    private final String prefix = Formatting.GOLD + "[Aurora]";
    public final int priority = PriorityUtils.get(this);

    public BlackOutModule(Category category, String name, String description) {
        super(category, name, description);
    }

    public void sendToggledMsg() {
        if (Config.get().chatFeedback.get().booleanValue() && this.chatFeedback && this.mc.world != null) {
            ChatUtils.forceNextPrefixClass(((Object) this).getClass());
            String msg = this.prefix + " " + Formatting.BLUE + this.name + (this.isActive() ? Formatting.GREEN + " Enabled" : Formatting.RED + " Disabled");
            this.sendMessage(Text.of(msg), this.hashCode());
        }
    }

    public static boolean nullCheck() {
        return Util.mc.player == null || Util.mc.world == null;
    }

    public void sendToggledMsg(String message) {
        if (Config.get().chatFeedback.get().booleanValue() && this.chatFeedback && this.mc.world != null) {
            ChatUtils.forceNextPrefixClass(((Object) this).getClass());
            String msg = this.prefix + " " + Formatting.BLUE + this.name + (this.isActive() ? Formatting.GREEN + " Enabled " : Formatting.RED + " Disabled ");
            this.sendMessage(Text.of(msg), this.hashCode());
        }
    }

    public void sendDisableMsg(String text) {
        if (this.mc.world != null) {
            ChatUtils.forceNextPrefixClass(((Object) this).getClass());
            String msg = this.prefix + " " + Formatting.BLUE + this.name + Formatting.RED + " Disabled " + Formatting.GRAY + text;
            this.sendMessage(Text.of(msg), this.hashCode());
        }
    }

    public void sendBOInfo(String text) {
        if (this.mc.world != null) {
            ChatUtils.forceNextPrefixClass(((Object) this).getClass());
            String msg = this.prefix + " " + Formatting.WHITE + this.name + " " + text;
            this.sendMessage(Text.of(msg), Objects.hash(this.name + "-info"));
        }
    }

    public void debug(String text) {
        if (this.mc.world != null) {
            ChatUtils.forceNextPrefixClass(((Object) this).getClass());
            String msg = this.prefix + " " + Formatting.WHITE + this.name + " " + Formatting.AQUA + text;
            this.sendMessage(Text.of(msg), 0);
        }
    }

    public void sendMessage(Text text, int id) {
        ((IChatHud)this.mc.inGameHud.getChatHud()).meteor$add(text, id);
    }

    public void sendPacket(Packet<?> packet) {
        if (this.mc.getNetworkHandler() == null) {
            return;
        }
        this.mc.getNetworkHandler().sendPacket(packet);
    }

    public void sendSequenced(SequencedPacketCreator packetCreator) {
        if (this.mc.interactionManager == null || this.mc.world == null || this.mc.getNetworkHandler() == null) {
            return;
        }
        PendingUpdateManager sequence = this.mc.world.getPendingUpdateManager().incrementSequence();
        Packet packet = packetCreator.predict(sequence.getSequence());
        this.mc.getNetworkHandler().sendPacket(packet);
        sequence.close();
    }

    public void placeBlock(Hand hand, Vec3d blockHitVec, Direction blockDirection, BlockPos pos) {
        Vec3d eyes = this.mc.player.getEyePos();
        boolean inside = eyes.x > (double)pos.getX() && eyes.x < (double)(pos.getX() + 1) && eyes.y > (double)pos.getY() && eyes.y < (double)(pos.getY() + 1) && eyes.z > (double)pos.getZ() && eyes.z < (double)(pos.getZ() + 1);
        SettingUtils.swing(SwingState.Pre, SwingType.Placing, hand);
        this.sendSequenced(s -> new PlayerInteractBlockC2SPacket(hand, new BlockHitResult(blockHitVec, blockDirection, pos, inside), s));
        SettingUtils.swing(SwingState.Post, SwingType.Placing, hand);
    }

    public void interactBlock(Hand hand, Vec3d blockHitVec, Direction blockDirection, BlockPos pos) {
        Vec3d eyes = this.mc.player.getEyePos();
        boolean inside = eyes.x > (double)pos.getX() && eyes.x < (double)(pos.getX() + 1) && eyes.y > (double)pos.getY() && eyes.y < (double)(pos.getY() + 1) && eyes.z > (double)pos.getZ() && eyes.z < (double)(pos.getZ() + 1);
        SettingUtils.swing(SwingState.Pre, SwingType.Interact, hand);
        this.sendSequenced(s -> new PlayerInteractBlockC2SPacket(hand, new BlockHitResult(blockHitVec, blockDirection, pos, inside), s));
        SettingUtils.swing(SwingState.Post, SwingType.Interact, hand);
    }

    public void useItem(Hand hand) {
        SettingUtils.swing(SwingState.Pre, SwingType.Using, hand);
        this.sendSequenced(s -> new PlayerInteractItemC2SPacket(hand, s));
        SettingUtils.swing(SwingState.Post, SwingType.Using, hand);
    }

    public void clientSwing(SwingHand swingHand, Hand realHand) {
        Hand hand = switch (swingHand) {
            default -> throw new IncompatibleClassChangeError();
            case MainHand -> Hand.MAIN_HAND;
            case OffHand -> Hand.OFF_HAND;
            case RealHand -> realHand;
        };
        this.mc.player.swingHand(hand, true);
        Modules.get().get(SwingModifier.class).startSwing(hand);
    }

    public Setting<Boolean> addPauseEat(SettingGroup group) {
        return group.add(new BoolSetting.Builder()
            .name("Pause Eat")
            .description("Pauses when eating")
            .defaultValue(false)
            .build()
        );
    }
}
