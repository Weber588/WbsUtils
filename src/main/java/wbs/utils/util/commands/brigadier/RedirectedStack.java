package wbs.utils.util.commands.brigadier;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class RedirectedStack extends net.minecraft.commands.CommandSourceStack {
    public static void loopParents(CommandContext<CommandSourceStack> context, Consumer<CommandContext<CommandSourceStack>> run) {
        if (!(context.getSource() instanceof RedirectedStack redirectedStack)) {
            run.accept(context);
            return;
        }

        loopParents(redirectedStack.parentContext(), run);
        run.accept(context);
    }
    public static <T> List<T> get(CommandContext<CommandSourceStack> context, Function<CommandContext<CommandSourceStack>, T> function) {
        if (!(context.getSource() instanceof RedirectedStack redirectedStack)) {
            return List.of(function.apply(context));
        }

        List<T> nodes = new LinkedList<>(get(redirectedStack.parentContext(), function));
        nodes.add(function.apply(context));
        return nodes;
    }
    public static <T, C extends Collection<T>> C getCollection(CommandContext<CommandSourceStack> context, Function<CommandContext<CommandSourceStack>, C> function, Supplier<C> modifiableCSupplier) {
        if (!(context.getSource() instanceof RedirectedStack redirectedStack)) {
            return function.apply(context);
        }

        C modifiableC = modifiableCSupplier.get();
        modifiableC.addAll(getCollection(redirectedStack.parentContext(), function, modifiableCSupplier));
        modifiableC.addAll(function.apply(context));
        return modifiableC;
    }
    public static List<ParsedCommandNode<CommandSourceStack>> getNodes(CommandContext<CommandSourceStack> context) {
        return getCollection(context, CommandContext::getNodes, LinkedList::new);
    }
    public static CommandContext<CommandSourceStack> getRoot(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource() instanceof RedirectedStack redirectedStack)) {
            return context;
        }

        return getRoot(redirectedStack.parentContext());
    }

    private final CommandContext<CommandSourceStack> parentContext;

    public RedirectedStack(CommandContext<CommandSourceStack> parentContext) {
        CommandSourceStack check = parentContext.getSource();

        if (!(check instanceof net.minecraft.commands.CommandSourceStack copy)) {
            throw new IllegalArgumentException("Only nms-backed command sources are supported.");
        }

        super(
                copy.source,
                copy.getPosition(),
                copy.getRotation(),
                copy.getLevel(),
                copy.permissions(),
                copy.getTextName(),
                copy.getDisplayName(),
                copy.getServer(),
                copy.getEntity()
        );

        this.parentContext = parentContext;
    }

    public CommandContext<CommandSourceStack> parentContext() {
        return parentContext;
    }

    public <T> List<T> fromParents(Function<CommandContext<CommandSourceStack>, T> function) {
        return get(parentContext(), function);
    }

    public <T, C extends Collection<T>> C fromParents(Function<CommandContext<CommandSourceStack>, C> function, Supplier<C> modifiableCSupplier) {
        return getCollection(parentContext(), function, modifiableCSupplier);
    }
    public <T> List<T> fromParentsList(Function<CommandContext<CommandSourceStack>, List<T>> function) {
        return getCollection(parentContext(), function, LinkedList::new);
    }
}
