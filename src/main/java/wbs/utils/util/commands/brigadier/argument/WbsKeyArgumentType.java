package wbs.utils.util.commands.brigadier.argument;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import wbs.utils.util.plugin.WbsPlugin;

import java.util.List;
import java.util.function.Function;

public class WbsKeyArgumentType implements WbsArgumentType<NamespacedKey> {
    public static @NonNull NamespacedKey parseKey(WbsPlugin plugin, @NonNull String asString, @Nullable String defaultNamespace) throws CommandSyntaxException {
        defaultNamespace = defaultNamespace == null ? NamespacedKey.MINECRAFT : defaultNamespace;
        if (!asString.contains(":")) {
            asString = defaultNamespace + ":" + asString;
        }

        NamespacedKey parsed = NamespacedKey.fromString(asString);

        if (parsed == null) {
            throw plugin.asException("Invalid key \"%s\"".formatted(asString));
        }

        return parsed;
    }

    private WbsPlugin plugin;
    private String defaultNamespace;
    private final @Nullable Function<CommandContext<CommandSourceStack>, Iterable<NamespacedKey>> suggestionProvider;

    public WbsKeyArgumentType(WbsPlugin plugin) {
        this(plugin, NamespacedKey.MINECRAFT_NAMESPACE);
    }

    public WbsKeyArgumentType(WbsPlugin plugin, @Nullable Function<CommandContext<CommandSourceStack>, Iterable<NamespacedKey>> suggestionProvider) {
        this(plugin, NamespacedKey.MINECRAFT_NAMESPACE, suggestionProvider);
    }

    public WbsKeyArgumentType(WbsPlugin plugin, String defaultNamespace) {
        this(plugin, defaultNamespace, null);
    }

    public WbsKeyArgumentType(WbsPlugin plugin, String defaultNamespace, @Nullable Function<CommandContext<CommandSourceStack>, Iterable<NamespacedKey>> suggestionProvider) {
        this.plugin = plugin;
        this.defaultNamespace = defaultNamespace;
        this.suggestionProvider = suggestionProvider;

        if (!Key.parseableNamespace(defaultNamespace)) {
            throw new IllegalArgumentException("Invalid namespace: %s".formatted(defaultNamespace));
        }
    }

    @Override
    public @NotNull String getSubstring(String string) {
        return string.split("\\s")[0];
    }

    @Override
    public @NonNull NamespacedKey parse(@NotNull String asString) throws CommandSyntaxException {
        return parseKey(plugin, asString, defaultNamespace);
    }

    @Override
    public Iterable<NamespacedKey> getSuggestions(CommandContext<CommandSourceStack> context) {
        return suggestionProvider != null ? suggestionProvider.apply(context) : List.of();
    }

    public String defaultNamespace() {
        return defaultNamespace;
    }

    public WbsKeyArgumentType defaultNamespace(String defaultNamespace) {
        this.defaultNamespace = defaultNamespace;
        return this;
    }

    @Override
    public String toString(NamespacedKey value) {
        return value.asString();
    }
}
