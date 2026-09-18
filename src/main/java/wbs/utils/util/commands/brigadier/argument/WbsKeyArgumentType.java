package wbs.utils.util.commands.brigadier.argument;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Function;

public class WbsKeyArgumentType implements WbsArgumentType<NamespacedKey> {
    public static @NonNull NamespacedKey parseKey(@NonNull String asString, @Nullable String defaultNamespace) throws CommandSyntaxException {
        defaultNamespace = defaultNamespace == null ? NamespacedKey.MINECRAFT : defaultNamespace;
        if (!asString.contains(":")) {
            asString = defaultNamespace + ":" + asString;
        }

        NamespacedKey parsed = NamespacedKey.fromString(asString);

        if (parsed == null) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("Invalid key \"%s\"".formatted(asString));
        }

        return parsed;
    }

    private final String defaultNamespace;
    private final @Nullable Function<CommandContext<CommandSourceStack>, Iterable<NamespacedKey>> suggestionProvider;

    public WbsKeyArgumentType() {
        this(NamespacedKey.MINECRAFT_NAMESPACE);
    }

    public WbsKeyArgumentType(@Nullable Function<CommandContext<CommandSourceStack>, Iterable<NamespacedKey>> suggestionProvider) {
        this(NamespacedKey.MINECRAFT_NAMESPACE, suggestionProvider);
    }

    public WbsKeyArgumentType(String defaultNamespace) {
        this(defaultNamespace, null);
    }

    public WbsKeyArgumentType(String defaultNamespace, @Nullable Function<CommandContext<CommandSourceStack>, Iterable<NamespacedKey>> suggestionProvider) {
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
        return parseKey(asString, defaultNamespace);
    }

    @Override
    public Iterable<NamespacedKey> getSuggestions(CommandContext<CommandSourceStack> context) {
        return suggestionProvider != null ? suggestionProvider.apply(context) : List.of();
    }

    @Override
    public String toString(NamespacedKey value) {
        return value.asString();
    }
}
