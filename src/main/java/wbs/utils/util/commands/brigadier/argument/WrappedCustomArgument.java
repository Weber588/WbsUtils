package wbs.utils.util.commands.brigadier.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface WrappedCustomArgument<T, N> extends CustomArgumentType<T, N> {
    CustomArgumentType<T, N> getWrappedArgumentType();

    @Override
    default T parse(StringReader reader) throws CommandSyntaxException {
        return getWrappedArgumentType().parse(reader);
    }

    @Override
    default ArgumentType<N> getNativeType() {
        return getWrappedArgumentType().getNativeType();
    }
}
