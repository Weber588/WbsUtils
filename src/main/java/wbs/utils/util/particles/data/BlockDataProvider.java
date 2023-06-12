package wbs.utils.util.particles.data;

import com.google.common.annotations.Beta;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundGroup;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.exceptions.InvalidConfigurationException;
import wbs.utils.exceptions.MissingRequiredKeyException;
import wbs.utils.util.providers.Provider;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  Provider that implements {@link BlockData} for the purpose of allowing
 *  particle data to be refreshed and written to a config.
 */
@Beta
@SuppressWarnings("unused")
public class BlockDataProvider implements BlockData, Provider {

    @NotNull
    private BlockData current;
    private int index = 0;
    private final List<BlockData> dataList = new LinkedList<>();

    public BlockDataProvider(Collection<BlockData> data) {
        dataList.addAll(data);
        current = dataList.get(0);
    }

    /**
     * @param section The config to read from.
     * @param path The path within the given config section to read from.
     * @throws InvalidConfigurationException If the config is misconfigured in an unrecoverable way.
     */
    public BlockDataProvider(ConfigurationSection section, String path) throws InvalidConfigurationException {
        String currentString = section.getString(path + ".current");
        if (currentString == null)
            throw new MissingRequiredKeyException("current is a required field.");

        try {
            current = Bukkit.getServer().createBlockData(currentString);
        } catch (IllegalArgumentException e) {
            throw new InvalidConfigurationException("Invalid block data (" + currentString + "): " + e.getMessage());
        }

        index = section.getInt(path + ".index", index);

        List<String> asStrings = section.getStringList(path + ".data-list");
        for (String asString : asStrings) {
            try {
                dataList.add(Bukkit.getServer().createBlockData(asString));
            } catch (IllegalArgumentException e) {
                throw new InvalidConfigurationException("Invalid block data (" + asString + "): " + e.getMessage());
            }
        }
    }

    @Override
    public void refresh() {
        index++;
        index %= dataList.size();

        current = dataList.get(index);
    }

    @Override
    public void writeToConfig(ConfigurationSection section, String path) {
        section.set(path + ".current", getAsString());
        section.set(path + ".index", index);

        List<String> asStrings = dataList.stream()
                .map(BlockData::getAsString)
                .collect(Collectors.toList());

        section.set(path + ".data-list", asStrings);
    }

    // Delegate methods to current

    //region Delegated methods
    @NotNull
    @Override
    public Material getMaterial() {
        return current.getMaterial();
    }

    @NotNull
    @Override
    public String getAsString() {
        return current.getAsString();
    }

    @NotNull
    @Override
    public String getAsString(boolean b) {
        return current.getAsString(b);
    }

    @NotNull
    @Override
    public BlockData merge(@NotNull BlockData blockData) {
        return current.merge(blockData);
    }

    @Override
    public boolean matches(@Nullable BlockData blockData) {
        return current.matches(blockData);
    }

    @NotNull
    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public BlockData clone() {
        return current.clone();
    }

    @NotNull
    @Override
    public SoundGroup getSoundGroup() {
        return current.getSoundGroup();
    }
    //endregion
}
