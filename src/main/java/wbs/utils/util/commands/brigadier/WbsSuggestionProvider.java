package wbs.utils.util.commands.brigadier;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@SuppressWarnings({"UnstableApiUsage", "unused"})
public interface WbsSuggestionProvider<T> extends SuggestionProvider<CommandSourceStack> {
    static <T> StaticKeysProvider<T> getStatic(@NotNull Iterable<T> values, @NotNull Function<T, String> toString, @Nullable String tooltip) {
        return new StaticKeysProvider<>(values, toString, tooltip);
    }
    static <T> StaticKeysProvider<T> getStatic(@NotNull Iterable<T> values, @NotNull Function<T, String> toString) {
        return getStatic(values, toString, null);
    }
    static <T> StaticKeysProvider<T> getStatic(@NotNull Iterable<T> values) {
        return getStatic(values, Objects::toString);
    }

    static boolean shouldSuggest(SuggestionsBuilder builder, String suggestion) {
        return suggestion.toLowerCase().startsWith(builder.getRemainingLowerCase());
    }
    static boolean shouldSuggest(SuggestionsBuilder builder, String ... matches) {
        return Arrays.stream(matches).anyMatch(match -> shouldSuggest(builder, match));
    }
    static boolean shouldSuggest(SuggestionsBuilder builder, Collection<String> matches) {
        return matches.stream().anyMatch(match -> shouldSuggest(builder, match));
    }

    Iterable<T> getSuggestions(CommandContext<CommandSourceStack> context);
    String toString(T value);
    default Collection<String> getSuggestionMatches(T value) {
        return Collections.singleton(toString(value));
    }
    default @Nullable String getDefaultTooltip() {
        return null;
    }

    @Override
    default CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        for (T value : getSuggestions(context)) {
            String stringValue = toString(value);
            if (shouldSuggest(builder, getSuggestionMatches(value))) {
                if (getDefaultTooltip() != null) {
                    builder.suggest(stringValue, this::getDefaultTooltip);
                } else {
                    builder.suggest(stringValue);
                }
            }
        }

        return builder.buildFuture();
    }

    final class StaticKeysProvider<T> implements WbsSuggestionProvider<T> {
        private final @NotNull Iterable<T> staticKeyed;
        private final @NotNull Function<T, String> toString;
        private final @Nullable String tooltip;

        public StaticKeysProvider(@NotNull Iterable<T> staticKeyed, @NotNull Function<T, String> toString, @Nullable String tooltip) {
            this.staticKeyed = staticKeyed;
            this.toString = toString;
            this.tooltip = tooltip;
        }

        @Override
        public @Nullable String getDefaultTooltip() {
            return tooltip;
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
