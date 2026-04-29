package wbs.utils.util.configuration.conditions.comparisons;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.plugin.WbsSettings;

import java.util.Objects;

public class EqualityComparison extends BinaryComparison {
    public EqualityComparison(ConfigurationSection parent, String key, @Nullable WbsSettings settings, @Nullable String directory) {
        super(parent, key, settings, directory);
    }

    @Override
    public boolean compare(Object object) {
        return Objects.equals(object, comparisonValue);
    }

    @Override
    public boolean numericOperator() {
        return false;
    }
}
