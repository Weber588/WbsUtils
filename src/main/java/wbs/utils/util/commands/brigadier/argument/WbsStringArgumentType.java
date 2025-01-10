package wbs.utils.util.commands.brigadier.argument;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface WbsStringArgumentType extends WbsArgumentType<String> {
    static WbsStringArgumentType word() {
        return (string) -> string.split(" ")[0];
    }
    static WbsStringArgumentType regexWord(String regex) {
        return new WbsStringArgumentType() {
            @Override
            public @NotNull String getSubstring(String string) {
                return string.split(" ")[0];
            }

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
}
