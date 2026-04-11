package wbs.utils.util.configuration.conditions;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import wbs.utils.util.plugin.WbsSettings;

import java.util.List;

public class AnyCondition extends ConditionAggregator {
    public AnyCondition(ConfigurationSection parent, String key, @Nullable WbsSettings settings, @Nullable String directory) {
        super(parent, key, settings, directory);
    }

    public AnyCondition(List<ConfigurableCondition> entryConditions) {
        super(entryConditions);
    }

    @Override
    public boolean testGeneric(Object object) {
        return conditions.stream().anyMatch(condition -> condition.test(object));
    }

    @Override
    protected @NotNull ConditionAggregator getNestedAggregator(List<ConfigurableCondition> conditions) {
        return new AllCondition(conditions);
    }
}
