package wbs.utils.util.configuration.conditions;

import io.papermc.paper.registry.RegistryKey;
import org.apache.commons.lang.math.NumberRange;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.generator.structure.StructureType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;
import wbs.utils.util.configuration.ConfigurableCondition;
import wbs.utils.util.configuration.ConfigurableContext;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.plugin.WbsSettings;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public abstract class LocationCondition implements ConfigurableCondition {
    private final List<BlockType> types;
    private List<Biome> biomes = new LinkedList<>();
    private List<StructureType> structures = new LinkedList<>();
    private @Nullable NumberRange x;
    private @Nullable NumberRange y;
    private @Nullable NumberRange z;

    public LocationCondition(WbsSettings settings, String directory, ConfigurationSection parent, String key) {
        ConfigurationSection section = parent.getConfigurationSection(key);
        if (section == null) {
            types = WbsConfigReader.getRegistryEntries(parent, key, RegistryKey.BLOCK);
            return;
        }

        types = WbsConfigReader.getRegistryEntries(parent, "type", RegistryKey.BLOCK);
        biomes = WbsConfigReader.getRegistryEntries(section, "biome", RegistryKey.BIOME);
        structures = WbsConfigReader.getRegistryEntries(section, "structure", RegistryKey.STRUCTURE_TYPE);

        x = WbsConfigReader.getNumberRange(section, "x");
        y = WbsConfigReader.getNumberRange(section, "y");
        z = WbsConfigReader.getNumberRange(section, "z");
    }

    @Nullable
    protected abstract Location getLocation(ConfigurableContext context);

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean test(ConfigurableContext context) {
        Location location = getLocation(context);
        if (location == null) {
            return false;
        }

        Block block = location.getBlock();

        if (!types.isEmpty() && !types.contains(Objects.requireNonNull(block.getType().asBlockType()))) {
            return false;
        }

        if (!biomes.isEmpty() && !biomes.contains(block.getBiome())) {
            return false;
        }

        if (!structures.isEmpty()) {
            boolean inStructure = block.getChunk().getStructures().stream()
                    .anyMatch(generated -> {
                        if (structures.contains(generated.getStructure().getStructureType())) {
                            return generated.getBoundingBox().contains(block.getLocation().toVector());
                        }
                        return false;
                    });

            if (!inStructure) {
                return false;
            }
        }

        if (x != null && !x.containsNumber(location.getX())) {
            return false;
        }

        if (y != null && !y.containsNumber(location.getY())) {
            return false;
        }

        if (z != null && !z.containsNumber(location.getZ())) {
            return false;
        }


        return true;
    }

    public static class ExactLocationCondition extends LocationCondition {
        public ExactLocationCondition(WbsSettings settings, String directory, ConfigurationSection parent, String key) {
            super(settings, directory, parent, key);
        }

        @Override
        protected Location getLocation(ConfigurableContext context) {
            return context.getLocation();
        }
    }

    public static class EntityEyeLocationCondition extends LocationCondition {
        public EntityEyeLocationCondition(WbsSettings settings, String directory, ConfigurationSection parent, String key) {
            super(settings, directory, parent, key);
        }

        @Override
        @Nullable
        protected Location getLocation(ConfigurableContext context) {
            if (context.getEntity() instanceof LivingEntity entity) {
                return entity.getEyeLocation();
            }

            return null;
        }
    }

    public static class BelowLocationCondition extends LocationCondition {
        public BelowLocationCondition(WbsSettings settings, String directory, ConfigurationSection parent, String key) {
            super(settings, directory, parent, key);
        }

        @Override
        protected Location getLocation(ConfigurableContext context) {
            return context.getLocation().subtract(0, 1, 0);
        }
    }

    public static class AboveLocationCondition extends LocationCondition {
        public AboveLocationCondition(WbsSettings settings, String directory, ConfigurationSection parent, String key) {
            super(settings, directory, parent, key);
        }

        @Override
        protected Location getLocation(ConfigurableContext context) {
            Entity entity = context.getEntity();
            if (entity != null) {
                return entity.getLocation().add(0, entity.getHeight() + 1, 0);
            }

            return context.getLocation().add(0, 1, 0);
        }
    }
}
