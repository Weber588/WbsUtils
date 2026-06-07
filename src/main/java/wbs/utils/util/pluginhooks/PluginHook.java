package wbs.utils.util.pluginhooks;

public class PluginHook implements PluginHookWrapper {
    private final String pluginName;

    public PluginHook(String pluginName) {
        this.pluginName = pluginName;
    }

    @Override
    public String getPluginName() {
        return pluginName;
    }
}
