package wbs.utils.util.particles.data;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import wbs.utils.exceptions.InvalidConfigurationException;
import wbs.utils.util.WbsEnums;
import wbs.utils.util.providers.Provider;

import java.util.LinkedList;
import java.util.List;

/**
 *  Provider that extends {@link ItemStack} for the purpose of allowing
 *  particle data to be refreshed and written to a config.
 */
@ApiStatus.Experimental
@SuppressWarnings("unused")
public class ItemStackProvider extends ItemStack implements Provider {

    private int index = 0;
    private final List<Material> dataList = new LinkedList<>();

    /**
     * @param data The {@link Material}s to be iterated over.
     */
    public ItemStackProvider(List<Material> data) {
        super(data.get(0));
        dataList.addAll(data);
    }

    /**
     * @param section The config to read from.
     * @param path The path within the given config section to read from.
     * @throws InvalidConfigurationException If the config is misconfigured in an unrecoverable way.
     */
    public ItemStackProvider(ConfigurationSection section, String path) throws InvalidConfigurationException {
        index = section.getInt(path + ".index");

        List<String> asStrings = section.getStringList(path + ".materials");
        for (String asString : asStrings) {
            Material check = WbsEnums.materialFromString(asString);

            if (check == null)
                throw new InvalidConfigurationException("Invalid material: " + asString);

            dataList.add(check);
        }
    }

    @Override
    public void writeToConfig(ConfigurationSection section, String path) {
        section.set(path + ".index", index);
        section.set(path + ".materials", dataList);
    }

    @Override
    public void refresh() {
        index++;
        index %= dataList.size();

        setType(dataList.get(index));
    }
}
