package wbs.utils.util.commands.brigadier;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@SuppressWarnings({"UnstableApiUsage", "unused"})
public abstract class WbsSuggestionProvider<T> implements SuggestionProvider<CommandSourceStack> {
    public static <T> StaticKeysProvider<T> getStatic(Iterable<T> values, Function<T, String> toString) {
        return new StaticKeysProvider<>(values, toString);
    }
    public static <T> StaticKeysProvider<T> getStatic(Iterable<T> values) {
        return new StaticKeysProvider<>(values, Objects::toString);
    }

    public static boolean shouldSuggest(SuggestionsBuilder builder, String suggestion) {
        return suggestion.toLowerCase().startsWith(builder.getRemainingLowerCase());
    }
    public static boolean shouldSuggest(SuggestionsBuilder builder, String ... matches) {
        return Arrays.stream(matches).anyMatch(match -> shouldSuggest(builder, match));
    }
    public static boolean shouldSuggest(SuggestionsBuilder builder, Collection<String> matches) {
        return matches.stream().anyMatch(match -> shouldSuggest(builder, match));
    }

    public abstract Iterable<T> getSuggestions(CommandContext<CommandSourceStack> context);
    public abstract String toString(T value);
    public Collection<String> getSuggestionMatches(T value) {
        return Collections.singleton(toString(value));
    }

    @Override
    public final CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        for (T value : getSuggestions(context)) {
            String stringValue = toString(value);
            if (shouldSuggest(builder, getSuggestionMatches(value))) {
                builder.suggest(stringValue);
            }
        }

        return builder.buildFuture();
    }

    public static final class StaticKeysProvider<T> extends WbsSuggestionProvider<T> {
        private final Iterable<T> staticKeyed;
        private final Function<T, String> toString;

        public StaticKeysProvider(Iterable<T> staticKeyed, Function<T, String> toString) {
            this.staticKeyed = staticKeyed;
            this.toString = toString;
        }

        @Override
        public Iterable<T> getSuggestions(CommandContext<CommandSourceStack> context) {
            return staticKeyed;
        }

        @Override
        public String toString(T value) {
            return toString.apply(value);
        }
    }
}
