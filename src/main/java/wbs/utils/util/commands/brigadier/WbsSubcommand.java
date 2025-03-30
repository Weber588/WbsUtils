package wbs.utils.util.commands.brigadier;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.commands.brigadier.argument.WbsSimpleArgument;
import wbs.utils.util.plugin.WbsPlugin;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

@SuppressWarnings("UnstableApiUsage")
public abstract class WbsSubcommand implements HoverEventSource<Component> {
    public static WbsSubcommand simpleSubcommand(WbsPlugin plugin, String label, Runnable executor) {
        return simpleSubcommand(plugin, label, context -> executor.run());
    }
    public static WbsSubcommand simpleSubcommand(WbsPlugin plugin, String label, Consumer<CommandContext<CommandSourceStack>> executor) {
        return simpleResponsiveSubcommand(plugin, label, context -> {
            executor.accept(context);
            return Command.SINGLE_SUCCESS;
        });
    }
    public static WbsSubcommand simpleResponsiveSubcommand(WbsPlugin plugin, String label, Function<CommandContext<CommandSourceStack>, Integer> executor) {
        return new WbsSubcommand(plugin, label) {
            @Override
            protected int executeNoArgs(CommandContext<CommandSourceStack> context) {
                return executor.apply(context);
            }
        };
    }

    protected final WbsPlugin plugin;
    protected final String label;
    protected @Nullable String description;
    protected @Nullable String permission;
    protected final @NotNull List<WbsSimpleArgument<?>> simpleArguments = new LinkedList<>();

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

        if (!simpleArguments.isEmpty()) {
            RequiredArgumentBuilder<CommandSourceStack, ?> builderChain = WbsSimpleArgument.toBuilderChain(this::onSimpleArgumentCallback, simpleArguments);
            // Override no args with simple arguments provided
            builder.executes(builderChain.getCommand());
            builder.then(builderChain);
        }

        return builder;
    }

    protected int onSimpleArgumentCallback(CommandContext<CommandSourceStack> context, WbsSimpleArgument.ConfiguredArgumentMap configuredArgumentMap) {
        return Command.SINGLE_SUCCESS;
    }

    public final WbsSubcommand addSimpleArgument(WbsSimpleArgument<?> argument) {
        this.simpleArguments.add(argument);
        return this;
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
