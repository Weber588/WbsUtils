package wbs.utils.util.configuration.conditions.comparisons;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.plugin.WbsSettings;

import java.math.BigDecimal;
import java.util.Objects;

public class GreaterThanComparison extends NumberComparison {
    public GreaterThanComparison(ConfigurationSection parent, String key, @Nullable WbsSettings settings, @Nullable String directory) {
        super(parent, key, settings, directory);
    }

    @Override
    protected boolean compareNumbers(Number number, Number comparisonNumber) {
        return number.doubleValue() > comparisonNumber.doubleValue();
    }
}
