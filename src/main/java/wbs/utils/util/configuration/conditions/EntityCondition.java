package wbs.utils.util.configuration.conditions;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.TagKey;
import org.apache.commons.lang.math.NumberRange;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.inventory.Merchant;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;
import wbs.utils.util.configuration.ConfigurableCondition;
import wbs.utils.util.configuration.ConfigurableContext;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.plugin.WbsSettings;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@NullMarked
public class EntityCondition implements ConfigurableCondition {
    private final List<EntityType> types;
    @Nullable
    private Boolean onFire;
    private NumberRange villagerLevel = new NumberRange(0L, Long.MAX_VALUE);
    private List<CreatureSpawnEvent.SpawnReason> spawnReasons = new LinkedList<>();

    public EntityCondition(WbsSettings settings, String directory, ConfigurationSection parent, String key) {
        ConfigurationSection section = parent.getConfigurationSection(key);
        if (section == null) {
            types = WbsConfigReader.getEnumList(parent, key, settings, directory, EntityType.class);
            return;
        }

        types = WbsConfigReader.getEnumList(section, "type", settings, directory, EntityType.class);
        onFire = WbsConfigReader.getBoolean(section, "on-fire", "burning");
        villagerLevel = WbsConfigReader.getNumberRange(section, "villager-level", villagerLevel);
        spawnReasons = WbsConfigReader.getEnumList(section, "spawn-reason", settings, directory, CreatureSpawnEvent.SpawnReason.class);
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean test(ConfigurableContext context) {
        Entity entity = context.getEntity();
        if (entity == null) {
            return false;
        }

        if (!types.isEmpty() && !types.contains(entity.getType())) {
            return false;
        }

        if (onFire != null && entity.getFireTicks() > 0 == onFire) {
            return false;
        }

        if (entity instanceof Villager villager && !villagerLevel.containsNumber(villager.getVillagerLevel())) {
            return false;
        }

        if (!spawnReasons.isEmpty() && !spawnReasons.contains(entity.getEntitySpawnReason())) {
            return false;
        }

        return true;
    }
}
