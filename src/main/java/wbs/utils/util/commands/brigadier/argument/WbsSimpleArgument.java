package wbs.utils.util.commands.brigadier.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.commands.brigadier.KeyedSuggestionProvider;
import wbs.utils.util.commands.brigadier.WbsSuggestionProvider;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings({"UnstableApiUsage", "unused"})
public class WbsSimpleArgument<T> {
    public static RequiredArgumentBuilder<CommandSourceStack, ?> toBuilderChain(BiFunction<CommandContext<CommandSourceStack>, ConfiguredArgumentMap, Integer> function,
                                                                                List<WbsSimpleArgument<?>> arguments) {
        LinkedList<WbsSimpleArgument<?>> cloned = new LinkedList<>(arguments);

        return cloned.removeFirst().asBuilder(function, cloned, arguments);
    }

    private final String label;
    private final ArgumentType<T> type;
    private final @Nullable T defaultValue;
    private final Class<T> clazz;
    protected final List<T> suggestions = new LinkedList<>();
    private Function<T, String> toString = Objects::toString;
    private @Nullable String tooltip = null;
    private @Nullable SuggestionProvider<CommandSourceStack> suggestionProvider;
    // Mainly used for generating usage strings -- not enforced.
    private boolean isRequired = true;

    public WbsSimpleArgument(String label, ArgumentType<T> type, @Nullable T defaultValue, Class<T> clazz) {
        this.label = label;
        this.type = type;
        this.defaultValue = defaultValue;
        this.clazz = clazz;
        this.suggestions.add(defaultValue);
    }

    @SafeVarargs
    public final <U extends T> WbsSimpleArgument<T> addSuggestions(U... suggestions) {
        this.suggestions.addAll(List.of(suggestions));
        return this;
    }

    public <U extends T> WbsSimpleArgument<T> addSuggestions(Collection<U> suggestions) {
        this.suggestions.addAll(suggestions);
        return this;
    }

    @SafeVarargs
    public final WbsSimpleArgument<T> setSuggestions(T... suggestions) {
        this.suggestions.clear();
        return addSuggestions(suggestions);
    }

    public WbsSimpleArgument<T> setSuggestions(Collection<T> suggestions) {
        this.suggestions.clear();
        return addSuggestions(suggestions);
    }

    public WbsSimpleArgument<T> setSuggestionProvider(@Nullable SuggestionProvider<CommandSourceStack> suggestionProvider) {
        this.suggestionProvider = suggestionProvider;
        return this;
    }

    private RequiredArgumentBuilder<CommandSourceStack, T> asBuilder(BiFunction<CommandContext<CommandSourceStack>, ConfiguredArgumentMap, Integer> function,
                                                                     List<WbsSimpleArgument<?>> next,
                                                                     final List<WbsSimpleArgument<?>> all) {
        RequiredArgumentBuilder<CommandSourceStack, T> builder = Commands.argument(label, type)
                .executes(context -> {
                    ConfiguredArgumentMap map = new ConfiguredArgumentMap();

                    for (WbsSimpleArgument<?> other : all) {
                        map.add(other.getConfigured(context));
                    }

                    return function.apply(context, map);
                })
                .suggests(suggestionProvider != null ? suggestionProvider : WbsSuggestionProvider.getStatic(suggestions, toString, tooltip));

        if (!next.isEmpty()) {
            WbsSimpleArgument<?> first = next.removeFirst();

            builder.then(first.asBuilder(function, next, all));
        }

        return builder;
    }

    public T getValue(CommandContext<?> context) {
        try {
            return context.getArgument(label, clazz);
        } catch (IllegalArgumentException ex) {
            return defaultValue;
        }
    }

    private ConfiguredArgument<T> getConfigured(CommandContext<?> context) {
        return new ConfiguredArgument<>(this, getValue(context));
    }

    public String label() {
        return label;
    }

    public ArgumentType<T> type() {
        return type;
    }

    public T defaultValue() {
        return defaultValue;
    }

    public Class<T> clazz() {
        return clazz;
    }

    public WbsSimpleArgument<T> setToString(Function<T, String> toString) {
        this.toString = toString;
        return this;
    }

    public WbsSimpleArgument<T> setTooltip(@Nullable String tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public void isRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    @NotNull
    public String getArgumentString() {
        if (isRequired) {
            return "<" + label + ">";
        } else {
            return "[" + label + "]";
        }
    }

    public static class KeyedSimpleArgument extends WbsSimpleArgument<NamespacedKey> {
        public KeyedSimpleArgument(String label, ArgumentType<NamespacedKey> type, NamespacedKey defaultValue) {
            super(label, type, defaultValue, NamespacedKey.class);
        }

        public <T extends Keyed> KeyedSimpleArgument setKeyedSuggestions(Collection<T> suggestions) {
            return (KeyedSimpleArgument) setSuggestions(suggestions.stream().map(Keyed::getKey).toList());
        }

        public <T extends Keyed> KeyedSimpleArgument addKeyedSuggestions(Collection<T> suggestions) {
            return (KeyedSimpleArgument) addSuggestions(suggestions.stream().map(Keyed::getKey).toList());
        }

        @Override
        public <U extends NamespacedKey> WbsSimpleArgument<NamespacedKey> addSuggestions(Collection<U> suggestions) {
            super.addSuggestions(suggestions);

            this.setSuggestionProvider(KeyedSuggestionProvider.getStaticKeyed(this.suggestions));

            return this;
        }

        @Override
        public WbsSimpleArgument<NamespacedKey> setSuggestions(Collection<NamespacedKey> suggestions) {
            super.setSuggestions(suggestions);

            this.setSuggestionProvider(KeyedSuggestionProvider.getStaticKeyed(this.suggestions));

            return this;
        }

        public <T extends Keyed> T getKeyedValue(CommandContext<CommandSourceStack> context, Function<NamespacedKey, T> getter) {
            return getter.apply(getValue(context));
        }
    }

    private record ConfiguredArgument<T>(WbsSimpleArgument<T> argument, T configuredValue) {
    }

    public static class ConfiguredArgumentMap {
        private final Set<ConfiguredArgument<?>> set = new HashSet<>();

        private void add(ConfiguredArgument<?> arg) {
            set.add(arg);
        }

        public <T> void add(WbsSimpleArgument<T> other, T value) {
            add(new ConfiguredArgument<>(other, value));
        }

        @SuppressWarnings("unchecked")
        public <T> T get(WbsSimpleArgument<T> argument) {
            ConfiguredArgument<?> configuredArgument = set.stream()
                    .filter(configured -> configured.argument.equals(argument))
                    .findAny()
                    .orElse(null);

            if (configuredArgument == null) {
                return argument.defaultValue;
            }

            return (T) configuredArgument.configuredValue;
        }
    }
}
