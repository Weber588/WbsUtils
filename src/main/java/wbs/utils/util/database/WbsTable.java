package wbs.utils.util.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.exceptions.WbsDatabaseException;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class WbsTable {

    private final List<WbsField> fields = new ArrayList<>();
    private final String tableName;
    private final WbsDatabase database;

    public WbsTable(WbsDatabase database, String tableName, @NotNull WbsField primaryKey) {
        this.database = database;
        this.tableName = tableName;
        // Primary key is always the first field
        addField(primaryKey);
        primaryKey.setPrimaryKey(true);
        primaryKey.setNotNull(true);

        database.addTable(this);
    }

    public void addField(WbsField ... field) {
        fields.addAll(Arrays.asList(field));
    }

    private String getAddFieldQuery(WbsField field) {
        return "ALTER TABLE " + tableName + " ADD " + field.getCreationPhrase();
    }

    public boolean addNewField(WbsField field) {
        if (database.queryWithoutReturns(getAddFieldQuery(field))) {
            fields.add(field);
            return true;
        } else {
            return false;
        }
    }

    public void addFieldIfNotExists(WbsField field) {
        String query = getAddFieldQuery(field);
        try (Connection connection = database.getConnection()) {
            if (connection == null) return;

            PreparedStatement statement = connection.prepareStatement(query);

            statement.execute();
            statement.close();
        } catch (SQLException e) {
            // TODO: Find a way to do this properly.
            if (e.getMessage().contains("duplicate column")) {
                // Add field, it already exists
                addField(field);
            }
        }
    }

    public String getCreationQuery() {
        String query = "CREATE TABLE IF NOT EXISTS " + tableName + " (";

        query += fields.stream()
                .map(WbsField::getCreationPhrase)
                .collect(Collectors.joining(", "));

        query += ");";
        return query;
    }

    public String getInsertStatement() {
        String query = "INSERT INTO " + tableName + " (";

        query += fields.stream()
                .map(WbsField::getFieldName)
                .collect(Collectors.joining(", "));

        query += ") VALUES (";

        query += fields.stream()
                .map(field -> "?")
                .collect(Collectors.joining(", "));

        query += ")";

        return query;
    }

    public String getUpdateStatement(String whereClaus) {
        String query = "UPDATE " + tableName + " SET ";

        query += fields.stream()
                .map(field -> field.getFieldName() + " = ?")
                .collect(Collectors.joining(", "));

        query += " WHERE " + whereClaus;

        return query;
    }

    @NotNull
    public List<WbsRecord> selectOnField(WbsField field, Object match) {
        List<WbsRecord> records = new ArrayList<>();

        String query = getSelectQuery(field.getFieldName() + " = ?");

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setObject(1, match);

            records = database.select(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return records;
    }

    public boolean upsert(WbsRecord record) {
        String query = getSelectQuery(getPrimaryKey().getFieldName() + " = ?");

        boolean recordExists;
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setObject(1, record.getValue(getPrimaryKey()));

            ResultSet set = statement.executeQuery();

            recordExists = set.next();

            if (recordExists) {
                return update(record, connection);
            } else {
                return insert(record, connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (WbsDatabaseException e) {
            database.getPlugin().logger.info("Failed to upsert. Query: " + e.getQuery());
            assert e.getWrapped() != null;
            e.getWrapped().printStackTrace();
        }
        return false;
    }

    /**
     * Inserts the given map of objects.
     * @param record The record to insert, with all fields populated.
     * @return True if the insert was successful, false if any key was missing.
     */
    public boolean insert(WbsRecord record) {
        try (Connection connection = database.getConnection()){
            return insert(record, connection);
        } catch (SQLException e) {
            database.getPlugin().logger.info("Failed to insert; connection error.");
            e.printStackTrace();
        } catch (WbsDatabaseException e) {
            database.getPlugin().logger.info("Failed to insert. Query: " + e.getQuery());
            assert e.getWrapped() != null;
            e.getWrapped().printStackTrace();
        }
        return false;
    }

    public boolean insert(WbsRecord record, Connection connection) throws WbsDatabaseException {
        String query = getInsertStatement();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            return runWithRecord(record, statement);
        } catch (SQLException e) {
            throw new WbsDatabaseException(e, query);
        }
    }

    public boolean update(WbsRecord record) {
        return update(record, getPrimaryKey().getFieldName() + " = ?");
    }

    /**
     * Inserts the given map of objects.
     * @param record The record to insert, with all fields populated.
     * @return True if the insert was successful, false if any key was missing.
     */
    public boolean update(WbsRecord record, String whereClause) {
        try (Connection connection = database.getConnection()){
            return update(record, connection, whereClause);
        } catch (SQLException e) {
            database.getPlugin().logger.info("Failed to update; connection error.");
            e.printStackTrace();
        } catch (WbsDatabaseException e) {
            database.getPlugin().logger.info("Failed to update. Query: " + e.getQuery());
            assert e.getWrapped() != null;
            e.getWrapped().printStackTrace();
        }
        return false;
    }

    public boolean update(WbsRecord record, Connection connection) throws WbsDatabaseException {
        return update(record, connection, getPrimaryKey().getFieldName() + " = ?");
    }

    public boolean update(WbsRecord record, Connection connection, String whereClause) throws WbsDatabaseException {
        String query = getUpdateStatement(whereClause);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            return runWithRecord(record, statement);
        } catch (SQLException e) {
            throw new WbsDatabaseException(e, query);
        }
    }

    private boolean runWithRecord(WbsRecord record, PreparedStatement statement) throws SQLException {
        // Prepared statements aren't zero-indexed; they start at 1
        int index = 1;

        ParameterMetaData metaData = statement.getParameterMetaData();

        while (index <= metaData.getParameterCount()) {
            for (WbsField field : fields) {
                if (index > metaData.getParameterCount()) {
                    break;
                }

                boolean hasDefault = field.getDefaultValue() != null;
                boolean fieldMissing = !record.hasValue(field) && field.requiresNotNull();
                if (fieldMissing) {
                    if (hasDefault) {
                        field.prepare(statement, index, field.getDefaultValue());
                    } else {
                        database.getPlugin().logger.info("Missing required field: " + field.getFieldName());
                        return false;
                    }
                } else {
                    field.prepare(statement, index, record.getValue(field));
                }

                index++;
            }
        }

        statement.executeUpdate();
        return true;
    }

    public String getName() {
        return tableName;
    }

    public String getSelectQuery(String whereClause) {
        return getSelectQuery("*", whereClause);
    }

    public String getSelectQuery(String fields, String whereClause) {
        return "SELECT " + fields + " FROM " + tableName + " WHERE " + whereClause;
    }

    @Nullable
    public WbsField getField(String fieldName) {
        for (WbsField checkField : fields) {
            if (checkField.getFieldName().equals(fieldName)) {
                return checkField;
            }
        }

        return null;
    }

    public WbsField getPrimaryKey() {
        return fields.get(0);
    }

    public List<WbsField> getFields() {
        return new ArrayList<>(fields);
    }
}
