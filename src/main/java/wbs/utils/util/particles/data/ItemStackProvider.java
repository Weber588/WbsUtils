package wbs.utils.util.particles.data;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import wbs.utils.exceptions.InvalidConfigurationException;
import wbs.utils.util.WbsEnums;
import wbs.utils.util.providers.Provider;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unused")
public class ItemStackProvider extends ItemStack implements Provider {

    private int index = 0;
    private final List<Material> dataList = new LinkedList<>();

    public ItemStackProvider(List<Material> data) {
        super(data.get(0));
        dataList.addAll(data);
    }

    public ItemStackProvider(ConfigurationSection section, String path) {
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
