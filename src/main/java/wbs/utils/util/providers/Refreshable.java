package wbs.utils.util.providers;

public interface Refreshable {
    /**
     * Refreshes this object, whatever that means for a given implementation - typically
     * an internal value that may be retrieved over time, but not necessarily.
     */
    void refresh();
}
