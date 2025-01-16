package wbs.utils.util.commands.brigadier.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.commands.brigadier.WbsSuggestionProvider;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings({"UnstableApiUsage", "unused"})
public interface WbsArgumentType<T> extends CustomArgumentType<T, String>, WbsSuggestionProvider<T> {
    @Override
    default @NotNull T parse(@NotNull StringReader reader) throws CommandSyntaxException {
        String asString = getSubstring(reader.getRemaining());
        reader.setCursor(reader.getCursor() + asString.length());
        
        return parse(asString);
    }

    /**
     * Get the starting substring of the provided String that will be used to parse into the {T}.
     * @param string The source string -- the remaining portion of the entered command
     * @return A substring from the start of the provided string, which will be parsed into {T}.
     * @throws CommandSyntaxException Any exceptions that occur during parsing
     */
    @NotNull String getSubstring(String string) throws CommandSyntaxException;
    @NotNull T parse(@NotNull String asString) throws CommandSyntaxException;

    @Override
    default @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.greedyString();
    }

    @Override
    default @NotNull <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        try {
            //noinspection unchecked
            return WbsSuggestionProvider.super.getSuggestions((CommandContext<CommandSourceStack>) context, builder);
        } catch (ClassCastException ex) {
            return CustomArgumentType.super.listSuggestions(context, builder);
        }
    }
}
