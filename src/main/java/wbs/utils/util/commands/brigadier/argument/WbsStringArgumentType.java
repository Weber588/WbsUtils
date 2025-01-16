package wbs.utils.util.commands.brigadier.argument;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
public interface WbsStringArgumentType extends WbsArgumentType<String> {
    static StringWordArgumentType word() {
        return new StringWordArgumentType() {};
    }
    static WbsStringArgumentType regexWord(String regex) {
        return new StringWordArgumentType() {
            @Override
            public boolean filter(@NotNull String string) {
                return string.matches(regex);
            }

            @Override
            public String errorFor(@NotNull String string) {
                return "String \"" + string + "\" does not match pattern " + regex;
            }
        };
    }

    default boolean filter(@NotNull String string) {
        return true;
    }
    default String errorFor(@NotNull String string) {
        return string;
    }

    @Override
    default @NotNull String parse(@NotNull String asString) throws CommandSyntaxException {
        if (filter(asString)) {
            return asString;
        }

        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create(errorFor(asString));
    }
    @Override
    default String toString(String value) {
        return value;
    }

    interface StringWordArgumentType extends WbsStringArgumentType, WbsWordArgumentType<String> {
        @Override
        default Iterable<String> getSuggestions(CommandContext<CommandSourceStack> context) {
            return List.of();
        }
    }
}
