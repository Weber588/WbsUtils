package wbs.utils.util.commands.brigadier;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.plugin.WbsPlugin;
import wbs.utils.util.plugin.WbsSettings;

import java.util.List;

@SuppressWarnings({"UnstableApiUsage", "unused"})
public abstract class WbsReloadSubcommand extends WbsSubcommand {
    public static WbsReloadSubcommand getStatic(WbsPlugin plugin, WbsSettings settings) {
        return (WbsReloadSubcommand) new AnonymousReloadCommand(plugin, settings).inferPermission();
    }

    public WbsReloadSubcommand(@NotNull WbsPlugin plugin) {
        super(plugin, "reload");
    }

    @Override
    protected int executeNoArgs(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();

        WbsSettings settings = getSettings();

        settings.getErrors().clear();
        settings.reload();

        List<String> errors = settings.getErrors();
        if (errors.isEmpty()) {
            plugin.sendMessage("&aReload successful!", sender);
        } else {
            plugin.sendMessage("&wThere were " + errors.size() + " config errors. Do &h/" + context.getInput().split(" ")[0] + " errors&w to see them.", sender);
        }

        return Command.SINGLE_SUCCESS;
    }

    protected abstract @NotNull WbsSettings getSettings();

    private static class AnonymousReloadCommand extends WbsReloadSubcommand {
        private final @NotNull WbsSettings settings;

        public AnonymousReloadCommand(@NotNull WbsPlugin plugin, @NotNull WbsSettings settings) {
            super(plugin);
            this.settings = settings;
        }

        @Override
        protected @NotNull WbsSettings getSettings() {
            return settings;
        }
    }
}
