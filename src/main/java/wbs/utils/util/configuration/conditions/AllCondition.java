package wbs.utils.util.configuration.conditions;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import wbs.utils.util.plugin.WbsSettings;

import java.util.Collection;
import java.util.List;

public class AllCondition extends ConditionAggregator {
    public AllCondition(ConfigurationSection parent, String key, @Nullable WbsSettings settings, @Nullable String directory) {
        super(parent, key, settings, directory);
    }

    public AllCondition(Collection<ConfigurableCondition> conditions) {
        super(conditions);
    }

    @Override
    public boolean testGeneric(Object object) {
        return conditions.stream().allMatch(condition -> condition.test(object));
    }

    @Override
    protected @NotNull ConditionAggregator getNestedAggregator(List<ConfigurableCondition> conditions) {
        return new AnyCondition(conditions);
    }
}
