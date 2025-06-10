package wbs.utils.util.effects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.plugin.WbsSettings;

public class ConfiguredBlockDisplay {
    public static @Nullable ConfiguredBlockDisplay fromConfigurationSection(WbsSettings settings, String directory, ConfigurationSection displaySection) {
        BlockData blockData;
        String displayName = displaySection.getName();

        String displayBlockDataString = displaySection.getString("block");

        if (displayBlockDataString == null) {
            settings.logError("\"block\" is a required field.", directory + "/block-displays/" + displayName + "/block");
            return null;
        }

        try {
            blockData = Bukkit.createBlockData(displayBlockDataString);
        } catch (IllegalArgumentException ex) {
            settings.logError("Invalid block data string: \"" + displayBlockDataString + "\".", directory + "/block-displays/" + displayName + "/block");
            return null;
        }

        Vector offset = WbsConfigReader.getVector(displaySection, "offset", new Vector(0, 0, 0));
        Vector scale = WbsConfigReader.getVector(displaySection, "scale", new Vector(1, 1, 1));

        return new ConfiguredBlockDisplay(displayName, blockData, offset, scale);
    }

    private final String name;
    private final BlockData blockData;
    private final Vector offset;

    private final Transformation transformation;

    public ConfiguredBlockDisplay(@NotNull String name, BlockData blockData, Vector offset, Vector scale) {
        this.name = name;
        this.blockData = blockData;
        this.offset = offset;

        Vector3f translation = new Vector3f((float) (-0.5 * scale.getX()), 0, (float) (-0.5 * scale.getZ()));
        AxisAngle4f zeroed = new AxisAngle4f(0f, 0f, 0f, 0f);
        transformation = new Transformation(translation, zeroed, scale.toVector3f(), zeroed);
    }

    public BlockDisplay spawn(Location origin, NamespacedKey parentKey) {
        return origin.getWorld().spawn(origin.clone().add(offset), BlockDisplay.class, display -> {
            display.setBlock(blockData);
            display.setTransformation(transformation);
            display.getPersistentDataContainer().set(parentKey, PersistentDataType.STRING, name);
        });
    }
}
