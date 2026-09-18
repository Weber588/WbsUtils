package wbs.utils.util.commands.brigadier.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;
import wbs.utils.WbsUtils;
import wbs.utils.exceptions.ThrowingFunction;
import wbs.utils.util.WbsRegistry;
import wbs.utils.util.commands.brigadier.KeyedSuggestionProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@NullMarked
public class WbsKeyedArgumentType<T extends Keyed> implements CustomArgumentType<T, NamespacedKey>, KeyedSuggestionProvider<T> {
    private final String typeName;
    private @Nullable String defaultNamespace = null;
    private final Function<NamespacedKey, @Nullable T> retriever;
    private @Nullable ThrowingFunction<CommandContext<CommandSourceStack>, Iterable<T>, CommandSyntaxException> suggestionProvider;

    public WbsKeyedArgumentType(String typeName, Function<NamespacedKey, @Nullable T> retriever) {
        this.typeName = typeName;
        this.retriever = retriever;
    }

    public <R extends WbsRegistry<T>> WbsKeyedArgumentType(String typeName, R registry) {
        this(typeName, (Function<NamespacedKey, T>) registry);
        suggestionProvider = (_ -> registry.values());
    }

    public <R extends Registry<T>> WbsKeyedArgumentType(String typeName, R registry) {
        this(typeName, (Function<NamespacedKey, @Nullable T>) registry::get);
        suggestionProvider = (_ -> registry.stream().toList());
    }

    @Override
    public T parse(StringReader reader) throws CommandSyntaxException {
        String asString = reader.getRemaining().split("\\s")[0];
        reader.setCursor(reader.getCursor() + asString.length());

        NamespacedKey key = WbsKeyArgumentType.parseKey(asString, defaultNamespace);

        T found = this.retriever.apply(key);

        if (found == null) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException()
                    .create("Invalid key \"" + key.asString() + "\" for type " + typeName);
        }
        return found;
    }

    @Override
    public ArgumentType<NamespacedKey> getNativeType() {
        return ArgumentTypes.namespacedKey();
    }

    @Override
    public Iterable<T> getSuggestions(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return suggestionProvider != null ? suggestionProvider.apply(context) : List.of();
    }

    public WbsKeyedArgumentType<T> setSuggestionProvider(@Nullable ThrowingFunction<CommandContext<CommandSourceStack>, Iterable<T>, CommandSyntaxException> suggestionProvider) {
        this.suggestionProvider = suggestionProvider;
        return this;
    }

    @Override
    public String toString(T value) {
        return value.getKey().asString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        try {
            //noinspection unchecked
            return KeyedSuggestionProvider.super.getSuggestions((CommandContext<CommandSourceStack>) context, builder);
        } catch (ClassCastException ex) {
            return CustomArgumentType.super.listSuggestions(context, builder);
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public @Nullable String defaultNamespace() {
        return defaultNamespace;
    }

    public WbsKeyedArgumentType<T> defaultNamespace(@Nullable String defaultNamespace) {
        this.defaultNamespace = defaultNamespace;
        return this;
    }
}
