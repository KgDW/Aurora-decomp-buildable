package kassuk.addon.aurora.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;

public class BlackoutGit extends Command
{
    public BlackoutGit() {
        super("blackoutinfo", "Gives the Blackout GitHub");
    }

    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            this.info("https://github.com/KassuK1/BlackOut");
            return 1;
        });
    }
}
