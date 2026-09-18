package wbs.utils.util.commands.brigadier.argument;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.commands.brigadier.WbsSuggestionProvider;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface WrappedSuggestionProvider<T> extends WbsSuggestionProvider<T> {
    default Collection<String> getSuggestionMatches(T value) {
        return getWrappedProvider().getSuggestionMatches(value);
    }

    WbsSuggestionProvider<T> getWrappedProvider();

    @Nullable
    default String getDefaultTooltip() {
        return getWrappedProvider().getDefaultTooltip();
    }

    default CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        return getWrappedProvider().getSuggestions(context, builder);
    }

    default boolean shouldSuggest(SuggestionsBuilder builder, String suggestion) {
        return getWrappedProvider().shouldSuggest(builder, suggestion);
    }

    default Iterable<T> getSuggestions(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return getWrappedProvider().getSuggestions(context);
    }

    default String toString(T value) {
        return getWrappedProvider().toString(value);
    }
}
