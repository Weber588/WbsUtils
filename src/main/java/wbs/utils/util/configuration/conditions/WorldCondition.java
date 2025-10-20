package wbs.utils.util.configuration.conditions;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.configuration.ConfigurableCondition;
import wbs.utils.util.configuration.ConfigurableContext;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.plugin.WbsSettings;

public class WorldCondition implements ConfigurableCondition {
    private final @Nullable String name;
    private Boolean raining;
    private @Nullable Boolean thundering;
    private World.Environment environment;

    public WorldCondition(WbsSettings settings, String directory, ConfigurationSection parent, String key) {
        ConfigurationSection section = parent.getConfigurationSection(key);
        if (section == null) {
            this.name = parent.getString(key);
            return;
        }

        name = section.getString("name");

        if (section.isBoolean("raining")) {
            raining = section.getBoolean("raining");
        }

        thundering = WbsConfigReader.getBoolean(section, "storming", "thundering");

        environment = WbsConfigReader.getEnum(section, "environment", settings, directory, World.Environment.class);
    }

    @Override
    public boolean test(ConfigurableContext context) {
        World world = context.getWorld();
        if (name != null && !world.getName().equals(name)) {
            return false;
        }

        if (raining != null && raining && !world.hasStorm()) {
            return false;
        }

        if (thundering != null && thundering && !world.isThundering()) {
            return false;
        }

        if (environment != null && world.getEnvironment() != environment) {
            return false;
        }

        return true;
    }
}
