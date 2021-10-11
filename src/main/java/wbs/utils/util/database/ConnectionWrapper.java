package wbs.utils.util.database;

import org.jetbrains.annotations.Nullable;
import wbs.utils.util.plugin.WbsPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SuppressWarnings("unused")
public class ConnectionWrapper {

    private final WbsPlugin plugin;
    private final WbsDatabase database;

    public ConnectionWrapper(WbsPlugin plugin, WbsDatabase database) {
        this.plugin = plugin;
        this.database = database;
    }

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
