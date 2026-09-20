package wbs.utils.util.commands.brigadier.argument;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.WbsRegistry;
import wbs.utils.util.plugin.WbsPlugin;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WbsRegistrySimpleArgument<T extends Keyed> extends WbsSimpleArgument<T> {
    private final WbsPlugin plugin;
    private final Function<NamespacedKey, T> getter;
    private @Nullable Component requiredMessage;

    public WbsRegistrySimpleArgument(String label, WbsPlugin plugin, String typeName, Class<T> tClass, Function<NamespacedKey, T> getter, Collection<T> allowed) {
        super(label, new WbsKeyedArgumentType<>(plugin, typeName, getter).defaultNamespace(plugin.namespace()), tClass);
        this.plugin = plugin;
        this.getter = getter;

        setSuggestions(allowed);
        setToString(t -> t.getKey().asString());
        requiredMessage(Component.text(
                "Choose a " + typeName + ": " +
                        allowed.stream()
                                .map(Keyed::key)
                                .map(Key::asString)
                                .collect(Collectors.joining(", "))
                )
        );
    }

    public WbsRegistrySimpleArgument(String label, WbsPlugin plugin, String typeName, Class<T> tClass, WbsRegistry<T> registry) {
        this(label, plugin, typeName, tClass, registry, registry.values());
    }

    public WbsRegistrySimpleArgument(String label, WbsPlugin plugin, String typeName, Class<T> tClass, Registry<T> registry) {
        this(label, plugin, typeName, tClass, registry::get, registry.stream().toList());
    }

    public WbsRegistrySimpleArgument<T> defaultValue(@Nullable NamespacedKey defaultValue) {
        return (WbsRegistrySimpleArgument<T>) super.defaultValue(getter.apply(defaultValue));
    }

    @Override
    public WbsRegistrySimpleArgument<T> defaultValue(T defaultValue) {
        return (WbsRegistrySimpleArgument<T>) super.defaultValue(defaultValue);
    }

    @Nullable
    public String defaultNamespace() {
        if (type() instanceof WbsKeyedArgumentType<?> keyedArgumentType) {
            return keyedArgumentType.defaultNamespace();
        }
        return null;
    }

    public WbsRegistrySimpleArgument<T> setDefaultNamespace(String defaultNamespace) {
        if (type() instanceof WbsKeyedArgumentType<?> keyedArgumentType) {
            keyedArgumentType.defaultNamespace(defaultNamespace);
        }
        return this;
    }

    public WbsRegistrySimpleArgument<T> isRequired(boolean isRequired) {
        return (WbsRegistrySimpleArgument<T>) super.isRequired(isRequired);
    }

    public @Nullable Component requiredMessage() {
        return requiredMessage;
    }

    public WbsSimpleArgument<T> requiredMessage(@Nullable Component requiredMessage) {
        this.requiredMessage = requiredMessage;
        return this;
    }

    @Nullable
    public T getRequiredValue(CommandContext<CommandSourceStack> context) {
        try {
            return context.getArgument(label(), clazz());
        } catch (IllegalArgumentException ex) {
            Component message;
            if (requiredMessage != null) {
                message = plugin.buildMessage(requiredMessage).toComponent();
            } else {
                message = Component.text(label() + " is a required argument.");
            }

            context.getSource().getSender().sendMessage(message);
            return null;
        }
    }
}
