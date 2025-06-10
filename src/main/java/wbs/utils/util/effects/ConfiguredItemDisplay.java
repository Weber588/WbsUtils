package wbs.utils.util.effects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.plugin.WbsSettings;

import java.util.function.Consumer;

public class ConfiguredItemDisplay {
    public static @Nullable ConfiguredItemDisplay fromConfigurationSection(WbsSettings settings, String directory, ConfigurationSection displaySection) {
        ItemStack item;
        String displayName = displaySection.getName();

        String displayItemString = displaySection.getString("item");

        if (displayItemString == null) {
            settings.logError("\"item\" is a required field.", directory + "/block-displays/" + displayName + "/block");
            return null;
        }

        try {
            item = Bukkit.getItemFactory().createItemStack(displayItemString);
        } catch (IllegalArgumentException ex) {
            settings.logError("Invalid block data string: \"" + displayItemString + "\".", directory + "/block-displays/" + displayName + "/block");
            return null;
        }

        Vector offset = WbsConfigReader.getVector(displaySection, "offset", new Vector(0, 0, 0));
        Vector scale = WbsConfigReader.getVector(displaySection, "scale", new Vector(1, 1, 1));

        return new ConfiguredItemDisplay(displayName, item, offset, scale);
    }

    private final String name;
    private final ItemStack item;
    private final Vector offset;

    private final Transformation transformation;

    public ConfiguredItemDisplay(@NotNull String name, ItemStack item, Vector offset, Vector scale) {
        this.name = name;
        this.item = item;
        this.offset = offset;

        Vector3f translation = new Vector3f(0, (float) scale.getY() * 0.5f, 0);
        AxisAngle4f zeroed = new AxisAngle4f(0f, 0f, 0f, 0f);
        transformation = new Transformation(translation, zeroed, scale.toVector3f(), zeroed);
    }

    public ConfiguredItemDisplay(@NotNull String name, ItemStack item, Transformation transformation) {
        this.name = name;
        this.item = item;
        this.offset = new Vector(0, 0, 0);
        this.transformation = transformation;
    }

    public ItemDisplay spawn(Location origin, NamespacedKey parentKey) {
        return spawn(origin, parentKey, null);
    }

    public ItemDisplay spawn(Location origin, NamespacedKey parentKey, Consumer<ItemDisplay> beforeSpawn) {
        return origin.getWorld().spawn(origin.clone().add(offset), ItemDisplay.class, display -> {
            display.setItemStack(item);
            display.setTransformation(transformation);
            display.getPersistentDataContainer().set(parentKey, PersistentDataType.STRING, name);

            if (beforeSpawn != null) {
                beforeSpawn.accept(display);
            }
        });
    }
}
