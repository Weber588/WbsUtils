package wbs.utils.util.configuration.conditions;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import wbs.utils.exceptions.InvalidConfigurationException;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.plugin.WbsSettings;

import java.util.*;

public abstract class ConditionAggregator implements GenericCondition {
    protected final Set<ConfigurableCondition> conditions = new HashSet<>();

    public ConditionAggregator(Collection<ConfigurableCondition> conditions) {
        this.conditions.addAll(conditions);
    }

    public ConditionAggregator(ConfigurationSection parent, String key, @Nullable WbsSettings settings, @Nullable String directory) {
        if (!parent.isList(key)) {
            throw new InvalidConfigurationException("Can only aggregate a list of conditions.", directory == null ? null : directory + "/" + key);
        }

        List<@NotNull ConfigurationSection> sectionList = WbsConfigReader.getSectionList(parent, key);

        for (int i = 0; i < sectionList.size(); i++) {
            ConfigurationSection section = sectionList.get(i);
            String entryDirectory = directory == null ? null : directory + "[" + i + "]";

            Set<String> keys = section.getKeys(false);

            List<ConfigurableCondition> entryConditions = new LinkedList<>();
            for (String entryKey : keys) {
                try {
                    ConfigurableCondition condition = ConfigurableConditionManager.buildCondition(section, entryKey, settings, entryDirectory);
                    entryConditions.add(condition);
                } catch (InvalidConfigurationException ex) {
                    if (settings != null) {
                        settings.logError(ex.getMessage(), ex.getDirectory());
                    }
                }
            }

            if (entryConditions.size() > 1) {
                // Combine all by default, opposite all/any behaviour. Default to ALL if unknown implementing class.
                ConditionAggregator condition = getNestedAggregator(entryConditions);
                conditions.add(condition);
            } else {
                conditions.addAll(entryConditions);
            }
        }

        if (conditions.isEmpty()) {
            throw new InvalidConfigurationException(parent.getName() + " must have at least 1 valid sub-condition.", directory);
        }
    }

    /// Get a condition aggregator with the given conditions, for use in aggregating map list entries that contain more than 1 condition.
    /// This should typically be the inverse, if applicable, of the implementing class.
    ///
    /// For example, the following section should be read differently depending on the implementor's nested aggregator:
    /// ```yaml
    /// root:
    ///   - A: {}
    ///     B: {}
    ///   - C: {}
    /// ```
    /// When `root` here is `any_of`, the (inverse) nested aggregator will be `all_of`, so it reads as `(A AND B) OR C`.
    ///
    /// When `root` here is `all_of`, the (inverse) nested aggregator will be `any_of`, so it reads as `(A OR B) AND C`.
    /// @param conditions The conditions to aggregate
    /// @return The inverse aggregator
    /// @implNote
    /// Any other implementations should aim to provide a distinct (preferably inverse) aggregator if at all possible, such that
    /// the following two sections do not evaluate identically. This allows more control without implicitly specifying the type of sub-entry conditions.
    /// ```yaml
    /// # 2 entries with 3 conditions
    /// root1:
    ///   - A: {}
    ///     B: {}
    ///   - C: {}
    /// # 3 entries with 3 conditions
    /// root2:
    ///   - A: {}
    ///   - B: {}
    ///   - C: {}
    /// ```
    protected abstract @NotNull ConditionAggregator getNestedAggregator(List<ConfigurableCondition> conditions);
}
