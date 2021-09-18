package wbs.utils.util.configuration.options;

import org.jetbrains.annotations.NotNull;
import wbs.utils.util.configuration.WbsConfig;
import wbs.utils.util.configuration.WbsConfigOption;

public class WbsIntOption extends WbsConfigOption<Integer, IntOption> {

    public WbsIntOption(@NotNull WbsConfig config, @NotNull IntOption annotation) {
        super(config, annotation);

        defaultValue = annotation.defaultValue();
    }
}
