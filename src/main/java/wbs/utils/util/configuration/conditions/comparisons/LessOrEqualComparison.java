package wbs.utils.util.configuration.conditions.comparisons;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.plugin.WbsSettings;

public class LessOrEqualComparison extends NumberComparison {
    public LessOrEqualComparison(ConfigurationSection parent, String key, @Nullable WbsSettings settings, @Nullable String directory) {
        super(parent, key, settings, directory);
    }

    @Override
    protected boolean compareNumbers(Number number, Number comparisonNumber) {
        return number.doubleValue() <= comparisonNumber.doubleValue();
    }
}
