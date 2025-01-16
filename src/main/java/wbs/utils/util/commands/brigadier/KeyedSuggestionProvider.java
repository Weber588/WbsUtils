package wbs.utils.util.commands.brigadier;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.key.Keyed;

import java.util.Collection;
import java.util.List;

@SuppressWarnings({"UnstableApiUsage", "unused"})
public interface KeyedSuggestionProvider<T extends Keyed> extends WbsSuggestionProvider<T> {
    static <T extends Keyed> KeyedSuggestionProvider<T> getStaticKeyed(Iterable<T> staticKeyed) {
        return new StaticKeysProvider<>(staticKeyed);
    }

    @Override
    default String toString(T value) {
        return value.key().asString();
    }

    @Override
    default Collection<String> getSuggestionMatches(T value) {
        return List.of(
                value.key().asString(),
                value.key().value()
        );
    }

    class StaticKeysProvider<T extends Keyed> implements KeyedSuggestionProvider<T> {
        private final Iterable<T> staticKeyed;

        public StaticKeysProvider(Iterable<T> staticKeyed) {
            this.staticKeyed = staticKeyed;
        }

        @Override
        public Iterable<T> getSuggestions(CommandContext<CommandSourceStack> context) {
            return staticKeyed;
        }
    }
}