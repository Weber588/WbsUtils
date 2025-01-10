package wbs.utils.util.commands.brigadier;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.plugin.WbsPlugin;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"UnstableApiUsage", "unused"})
public class WbsCommand extends WbsSubcommand {
    public static WbsCommand getStatic(@NotNull WbsPlugin plugin, @NotNull String label, @NotNull String description) {
        return new WbsCommand(plugin, label, description);
    }
    public static WbsCommand getStatic(@NotNull WbsPlugin plugin, @NotNull String label) {
        return new WbsCommand(plugin, label, "A " + plugin.getName() + " command.");
    }

    private final List<String> aliases = new LinkedList<>();
    private final Map<String, WbsSubcommand> subcommandMap = new HashMap<>();

    protected WbsCommand(@NotNull WbsPlugin plugin, @NotNull String label, @NotNull String description) {
        super(plugin, label);
        this.addAliases(label);
        this.description = description;
    }

    public WbsCommand addAliases(String ... aliases) {
        this.aliases.addAll(Arrays.asList(aliases));

        return this;
    }

    public WbsCommand addSubcommands(WbsSubcommand ... subcommands) {
        for (WbsSubcommand subcommand : subcommands) {
            subcommandMap.put(subcommand.getLabel(), subcommand);
        }

        return this;
    }

    public LiteralCommandNode<CommandSourceStack> build() {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(label);

        builder.requires(context -> plugin.isEnabled())
                .executes(this::executeNoArgs);

        addThens(builder);

        return builder.build();
    }

    @Override
    protected void addThens(LiteralArgumentBuilder<CommandSourceStack> builder) {
        if (permission != null) {
            builder.requires(context -> context.getSender().hasPermission(permission));
        }

        for (WbsSubcommand subcommand : subcommandMap.values()) {
            builder.then(subcommand.getArgument());
        }
    }

    public void register() {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            if (plugin.isEnabled()) {
                event.registrar().register(build(),
                        description,
                        aliases
                );
            }
        });
    }

    protected int executeNoArgs(CommandContext<CommandSourceStack> context) {
        List<Component> components = subcommandMap.values().stream().map(subcommand -> Component.text(subcommand.label)
                .color(plugin.getTextHighlightColour())
                .hoverEvent(subcommand)
        ).collect(Collectors.toUnmodifiableList());

        Component options = Component.join(JoinConfiguration.separator(Component.text(", ")), components);

        plugin.buildMessage("Usage: &h/" + context.getInput().split(" ")[0] + " <option>&r. Please choose from the following: ")
                .append(options)
                .send(context.getSource().getSender());

        return Command.SINGLE_SUCCESS;
    }
}
