package wbs.utils.util;


import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utility class for mapping material types based on concepts not existing in the game.
 * <br/>
 * Planned to be expanded in future to allow configured mappings between various concepts.
 */
@SuppressWarnings("unused")
public final class WbsMaterials {
    private WbsMaterials() {}

    @Nullable
    public static TreeSpecies getTreeSpecies(@NotNull Material material) {
        TreeSpecies species;

        Map<String, TreeSpecies> speciesMap = new LinkedHashMap<>();

        // Do this first to catch before Oak is checked
        speciesMap.put("DARK_OAK", TreeSpecies.DARK_OAK);

        speciesMap.put("OAK", TreeSpecies.GENERIC);
        speciesMap.put("BIRCH", TreeSpecies.BIRCH);
        speciesMap.put("SPRUCE", TreeSpecies.REDWOOD);
        speciesMap.put("JUNGLE", TreeSpecies.JUNGLE);
        speciesMap.put("ACACIA", TreeSpecies.ACACIA);

        String materialString = material.name().toUpperCase();
        for (String check : speciesMap.keySet()) {
            if (materialString.contains(check)) {
                return speciesMap.get(check);
            }
        }

        return null;
    }

}
