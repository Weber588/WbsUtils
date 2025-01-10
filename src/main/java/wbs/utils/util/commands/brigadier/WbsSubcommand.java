package wbs.utils.util.commands.brigadier;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.plugin.WbsPlugin;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

@SuppressWarnings("UnstableApiUsage")
public abstract class WbsSubcommand implements HoverEventSource<Component> {
    protected final WbsPlugin plugin;
    protected final String label;
    protected @Nullable String description;
    protected @Nullable String permission;

    public WbsSubcommand(@NotNull WbsPlugin plugin, @NotNull String label) {
        this.plugin = plugin;
        this.label = label;
    }

    public WbsSubcommand(@NotNull WbsPlugin plugin, @NotNull String label, @Nullable String permission) {
        this(plugin, label);
        this.permission = permission;
    }

    public final LiteralArgumentBuilder<CommandSourceStack> getArgument() {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(label)
                .requires(andPredicates(this::hasPermission, this::canRun))
                .executes(this::executeNoArgs);

        addThens(builder);

        return builder;
    }

    @SafeVarargs
    private <T> Predicate<T> andPredicates(Predicate<T> ... predicates) {
        return test -> (boolean) Arrays.stream(predicates)
                .map(predicate -> predicate.test(test))
                .reduce(true, (a, b) -> a && b);
    }

    protected abstract int executeNoArgs(CommandContext<CommandSourceStack> context);
    protected void addThens(LiteralArgumentBuilder<CommandSourceStack> builder) {

    }

    protected boolean canRun(CommandSourceStack context) {
        return true;
    }

    private boolean hasPermission(CommandSourceStack context) {
        if (permission == null) {
            return true;
        }
        return context.getSender().hasPermission(permission);
    }

    public String getLabel() {
        return label;
    }

    public @Nullable String getDescription() {
        return description;
    }

    @Override
    public @NotNull HoverEvent<Component> asHoverEvent(@NotNull UnaryOperator<Component> op) {
        if (description != null) {
            return HoverEvent.showText(op.apply(Component.text(label + ": " + description)));
        }

        return HoverEvent.showText(Component.text(label));
    }
}
