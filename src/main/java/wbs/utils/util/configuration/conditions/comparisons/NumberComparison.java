package wbs.utils.util.configuration.conditions.comparisons;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.plugin.WbsSettings;

public abstract class NumberComparison extends BinaryComparison {
    public NumberComparison(ConfigurationSection parent, String key, @Nullable WbsSettings settings, @Nullable String directory) {
        super(parent, key, settings, directory);
    }

    @Override
    public final boolean compare(Object object) {
        if (!(object instanceof Number number)) {
            throw new IllegalArgumentException("Attempted to compare non-number with a numeric operator.");
        }
        if (!(comparisonValue instanceof Number comparisonNumber)) {
            throw new IllegalStateException("Comparison value was non-numeric in numeric operator.");
        }

        return compareNumbers(number, comparisonNumber);
    }

    protected abstract boolean compareNumbers(Number number, Number comparisonNumber);

    @Override
    public boolean numericOperator() {
        return true;
    }
}
