package wbs.utils.util.commands.brigadier.argument;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.jspecify.annotations.NullMarked;
import wbs.utils.util.commands.brigadier.WbsSuggestionProvider;

import java.util.concurrent.CompletableFuture;

@NullMarked
public interface ArgumentTypeSuggestionProvider<T, N> extends CustomArgumentType<T, N>, WbsSuggestionProvider<T> {
    @Override
    default <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        try {
            //noinspection unchecked
            return WbsSuggestionProvider.super.getSuggestions((CommandContext<CommandSourceStack>) context, builder);
        } catch (ClassCastException ex) {
            return CustomArgumentType.super.listSuggestions(context, builder);
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
