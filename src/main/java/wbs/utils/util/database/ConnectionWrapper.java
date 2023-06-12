package wbs.utils.util.database;

import org.jetbrains.annotations.Nullable;
import wbs.utils.util.plugin.WbsPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Wraps a connection to an SQL database.
 */
@SuppressWarnings("unused")
public class ConnectionWrapper {

    private final WbsPlugin plugin;
    private final WbsDatabase database;

    /**
     * Creates a connection wrapper related to the given Wbs wrappers.
     * @param plugin The related WbsPlugin to use for threading and logging.
     * @param database The database wrapper.
     */
    public ConnectionWrapper(WbsPlugin plugin, WbsDatabase database) {
        this.plugin = plugin;
        this.database = database;
    }

    /**
     * @return An open connection from the related database, or null if an exception occurs.
     */
    @Nullable
    public Connection getConnection() {
        database.ensureFolderExists();

        try {
            return DriverManager.getConnection(database.getDatabasePath());
        } catch (SQLException dbException) {
            plugin.logger.info("Database failed to connect.");
            dbException.printStackTrace();
            return null;
        }
    }
}
