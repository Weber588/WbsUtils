package wbs.utils.util.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.exceptions.WbsDatabaseException;
import wbs.utils.util.plugin.WbsPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public class WbsDatabase {

    private final WbsPlugin plugin;

    private final String dbName;
    private final ConnectionWrapper connectionWrapper;

    private final Set<WbsTable> tables = new HashSet<>();

    public WbsDatabase(WbsPlugin plugin, String dbName) {
        this.plugin = plugin;
        this.dbName = dbName;

        connectionWrapper = new ConnectionWrapper(plugin, this);
    }

    public void addTable(WbsTable table) {
        tables.add(table);
    }

    @Nullable
    public WbsTable getTable(String tableName) {
        for (WbsTable table : tables) {
            if (table.getName().equalsIgnoreCase(tableName)) {
                return table;
            }
        }
        return null;
    }

    public String getDatabasePath() {
        return "jdbc:sqlite:" + getDatabaseFilePath();
    }

    public String getDatabaseFilePath() {
        return plugin.getDataFolder().getPath() + File.separator + dbName + ".db";
    }

    boolean ensureFolderExists() {
        if (!plugin.getDataFolder().exists()) {
            boolean success = plugin.getDataFolder().mkdir();
            if (!success) {
                plugin.logger.severe("Plugin folder failed to create");
            }
            return success;
        }
        return true;
    }

    public boolean databaseExists() {
        File file = new File(getDatabaseFilePath());

        return file.exists();
    }

    /**
     * Create a database if it doesn't already exist under the given plugin's
     * folder
     * @return True if the database exists (regardless of if it was created
     * or not). False if it didn't exist and failed to create.
     */
    public boolean createDatabase() {
        if (databaseExists()) return true;

        if (!ensureFolderExists()) {
            return false;
        }

        try (Connection connection = connectionWrapper.getConnection()) {
            if (connection == null) {
                plugin.logger.severe("Database \"" + dbName + "\" failed to create.");
            }
        } catch (SQLException e) {
            plugin.logger.severe("Database \"" + dbName + "\" failed to create.");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean createTables() {
        try (Connection connection = connectionWrapper.getConnection()) {
            if (connection == null) {
                return false;
            }

            for (WbsTable table : tables) {
                String tableCreationQuery = table.getCreationQuery();
                try {
                    connection.prepareStatement(tableCreationQuery).executeUpdate();

                } catch (SQLException e) {
                    plugin.logger.info("Query: " + tableCreationQuery);
                    throw e;
                }
            }

            return true;
        } catch (SQLException e) {
            plugin.logger.severe("Failed to create tables.");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Runs a query directly, auto-closing the connection used and
     * returning a list of WbsRecords from the results of the query. <p>
     * <b>This method does not close the given statement automatically.</b>
     * @param query The prepared statement to run.
     * @return A list of the results which may be empty. The list will also be
     * empty if an SQL exception is thrown during the query, but the exception
     * will not be thrown.
     * @throws WbsDatabaseException If the WbsRecord was not able to be created from the
     * ResultSet retrieved, which may occur if a field was created/retrieved without a
     * corresponding WbsField in any table.
     */
    @NotNull
    public List<WbsRecord> select(PreparedStatement query) throws WbsDatabaseException {
        List<WbsRecord> records = new ArrayList<>();
        try (Connection connection = connectionWrapper.getConnection()) {
            if (connection == null) {
                return records;
            }

            ResultSet set = query.executeQuery();

            while (set.next()) {
                WbsRecord foundRecord = new WbsRecord(this, set);
                records.add(foundRecord);
            }

            set.close();
            return records;
        } catch (SQLException e) {
            plugin.logger.severe("Selection failed: " + query);
            e.printStackTrace();
            return records;
        }
    }

    @Nullable
    public ResultSet query(String query) {
        try (Connection connection = connectionWrapper.getConnection()) {
            if (connection == null) return null;

            PreparedStatement statement = connection.prepareStatement(query);

            try {
                return statement.executeQuery();
            } catch (SQLException e) {
                statement.execute();
                return null;
            }
        } catch (SQLException e) {
            plugin.logger.severe("Query failed to run: " + query);
            e.printStackTrace();
            return null;
        }
    }

    public boolean queryWithoutReturns(String query) {
        try (Connection connection = connectionWrapper.getConnection()) {
            if (connection == null) return false;

            PreparedStatement statement = connection.prepareStatement(query);

            statement.execute();
            return true;
        } catch (SQLException e) {
            plugin.logger.severe("Query failed to run: " + query);
            e.printStackTrace();
            return false;
        }
    }

    @Nullable
    public WbsField getField(String tableName, String fieldName) {
        WbsTable table = null;
        for (WbsTable checkTable : tables) {
            if (checkTable.getName().equals(tableName)) {
                table = checkTable;
                break;
            }
        }

        if (table == null) {
            return null;
        }

        return table.getField(fieldName);
    }

    public Connection getConnection() {
        return connectionWrapper.getConnection();
    }

    public WbsPlugin getPlugin() {
        return plugin;
    }
}
