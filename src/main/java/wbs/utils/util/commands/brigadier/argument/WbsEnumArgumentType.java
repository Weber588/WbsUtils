package wbs.utils.util.commands.brigadier.argument;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.WbsEnums;

import java.util.List;

@SuppressWarnings({"UnstableApiUsage", "unused"})
public class WbsEnumArgumentType<T extends Enum<T>> implements WbsArgumentType<T> {
    private final Class<T> clazz;

    public WbsEnumArgumentType(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public @NotNull String getSubstring(String string) throws CommandSyntaxException {
        return string;
    }

    @Override
    public @NotNull T parse(@NotNull String asString) throws CommandSyntaxException {
        T enumValue = WbsEnums.getEnumFromString(clazz, asString);

        if (enumValue == null) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("Invalid value \"" + asString + "\" for " + clazz.getSimpleName());
        }

        return enumValue;
    }

    @Override
    public Iterable<T> getSuggestions(CommandContext<CommandSourceStack> context) {
        return List.of(clazz.getEnumConstants());
    }

    @Override
    public String toString(T value) {
        return value.name();
    }
}
