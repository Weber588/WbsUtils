package wbs.utils.util.configuration.conditions.comparisons;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.configuration.WbsValueReader;
import wbs.utils.util.plugin.WbsSettings;

public abstract class BinaryComparison implements ComparisonOperator {
    protected final Object comparisonValue;

    public BinaryComparison(ConfigurationSection parent, String key, @Nullable WbsSettings settings, @Nullable String directory) {
        ConfigurationSection section = parent.getConfigurationSection(key);

        WbsValueReader configReader;
        if (section == null) {
            configReader = new WbsValueReader(parent, key, directory);
        } else {
            configReader = new WbsValueReader(section, "value", directory);
        }

        configReader.settings(settings)
                .isRequired(true);

        if (configReader.isNumber()) {
            comparisonValue = configReader.readNumber();
        } else {
            if (numericOperator()) {
                comparisonValue = configReader
                        .setRequiredValueString("Comparison must be numeric.")
                        .readNumber();
            } else {
                comparisonValue = configReader.read();
            }
        }
    }
}
