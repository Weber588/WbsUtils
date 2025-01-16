package wbs.utils.util.commands.brigadier.argument;

import org.jetbrains.annotations.NotNull;

public interface WbsWordArgumentType<T> extends WbsArgumentType<T> {
    @Override
    default @NotNull String getSubstring(String string) {
        return string.split(" ")[0];
    }
}
