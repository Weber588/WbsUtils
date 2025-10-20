package wbs.utils.util.configuration;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public interface ConfigurableCondition extends Predicate<ConfigurableContext> {
    boolean test(ConfigurableContext context);
}
