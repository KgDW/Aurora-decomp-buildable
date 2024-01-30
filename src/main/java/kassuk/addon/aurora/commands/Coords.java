package kassuk.addon.aurora.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import meteordevelopment.meteorclient.MeteorClient;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;

public class Coords extends Command
{
    public Coords() {
        super("coords", "Copies your coordinates to your clipboard.");
    }

    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            if (MeteorClient.mc.player != null) {
                final String text = "x: " + Math.floor(MeteorClient.mc.player.getX()) + "; y:" + Math.floor(MeteorClient.mc.player.getY()) + "; z:" + Math.floor(MeteorClient.mc.player.getZ());
                this.info("Succesfully copied your coordinates: \n" + text);
                MeteorClient.mc.keyboard.setClipboard(text);
            }
            return 1;
        });
    }
}
