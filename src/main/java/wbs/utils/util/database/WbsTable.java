package wbs.utils.util.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.exceptions.WbsDatabaseException;

import java.sql.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class WbsTable {

    private final List<WbsField> fields = new ArrayList<>();
    private final String tableName;
    private final WbsDatabase database;

    private boolean debugMode = false;

    public WbsTable(WbsDatabase database, String tableName, @NotNull WbsField ... primaryKey) {
        this.database = database;
        this.tableName = tableName;

        addField(primaryKey);

        for (WbsField primary : primaryKey) {
            primary.setPrimaryKey(true);
            primary.setNotNull(true);
        }

        database.addTable(this);
    }

    public void addField(WbsField ... field) {
        fields.addAll(Arrays.asList(field));
    }

    public boolean removeField(WbsField field) {
        return fields.remove(field);
    }

    private String getAddFieldQuery(WbsField field) {
        return "ALTER TABLE " + tableName + " ADD " + field.getCreationPhrase();
    }

    /**
     * Add a new field, updating the underlying database.
     * @param field The field to add
     * @return True if the field was added successfully, or false if the field failed to
     * add for any reason.
     */
    public boolean addNewField(WbsField field) {
        if (database.queryWithoutReturns(getAddFieldQuery(field))) {
            fields.add(field);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Adds a field if it doesn't already exist.
     * @param field The field to try adding.
     * @return True if the field now exists, regardless of if it existed previously. False if there was an error.
     */
    public boolean addFieldIfNotExists(WbsField field) {
        String query = getAddFieldQuery(field);
        try (Connection connection = database.getConnection()) {
            if (connection == null) return false;

            PreparedStatement statement = connection.prepareStatement(query);

            if (debugMode) {
                database.getPlugin().logger.info("addFieldIfNotExists: " + statement);
            }

            statement.execute();

            addField(field);

            statement.close();
            return true;
        } catch (SQLException e) {
            // TODO: Find a way to do this properly.
            if (e.getMessage().contains("duplicate column")) {
                // Add field, it already exists
                addField(field);
                return true;
            } else {
                e.printStackTrace();
                return false;
            }
        }
    }

    private String getAlterColumnQuery(WbsField field) {
        return "ALTER TABLE " + tableName + " ALTER COLUMN " + field.getCreationPhrase();
    }

    /**
     * Update the definition of a field.<br/>
     * Note that this change is potentially destructive, as it require
     * @param field The field to update
     * @return True if the field was updated (or already existed as-is),
     * false if an error occurred or if the field didn't exist to be updated
     */
    public boolean updateField(WbsField field) {
        WbsField existingField = getField(field.getFieldName());

        if (existingField == null) {
            return false;
        }

        // If creation phrases match, no need to update - return true;
        if (existingField.getCreationPhrase().equalsIgnoreCase(field.getCreationPhrase())) {
            return true;
        }

        String query = getAlterColumnQuery(field);
        try (Connection connection = database.getConnection()) {
            if (connection == null) return false;

            PreparedStatement statement = connection.prepareStatement(query);

            if (debugMode) {
                database.getPlugin().logger.info("updateField: " + statement);
            }

            statement.execute();

            addField(field);
            removeField(existingField);

            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getCreationQuery() {
        String query = "CREATE TABLE IF NOT EXISTS " + tableName + " (";

        query += fields.stream()
                .map(WbsField::getCreationPhrase)
                .collect(Collectors.joining(", "));

        query += ", PRIMARY KEY (";

        query += fields.stream()
                .filter(WbsField::isPrimaryKey)
                .map(WbsField::getFieldName)
                .collect(Collectors.joining(", "));

        query += ")";
        query += ");";

        return query;
    }
    public String getInsertStatement() {
        return getInsertStatement(1);
    }

    public String getInsertStatement(int amount) {
        if (amount < 1) throw new IllegalArgumentException("Amount to insert must be positive.");

        String query = "INSERT INTO " + tableName + " (";

        query += fields.stream()
                .map(WbsField::getFieldName)
                .collect(Collectors.joining(", "));

        query += ") VALUES ";

        String valueQuery = "(" + fields.stream()
                .map(field -> "?")
                .collect(Collectors.joining(", ")) + ")";

        StringBuilder fullValues = new StringBuilder();
        for (int i = 0; i < amount; i++) {
            fullValues.append(valueQuery).append(", ");
        }
        query += fullValues.substring(0, fullValues.length() - 3);

        query += ")";

        return query;
    }

    public String getUpdateStatement(@NotNull String whereClaus) {
        String query = "UPDATE " + tableName + " SET ";

        query += fields.stream()
                .map(field -> field.getFieldName() + " = ?")
                .collect(Collectors.joining(", "));

        query += " WHERE " + whereClaus;

        return query;
    }

    @NotNull
    public List<WbsRecord> selectOnField(@NotNull WbsField field, @Nullable Object match) {
        return selectOnField(field, match, null);
    }

    @NotNull
    public List<WbsRecord> selectOnField(@NotNull WbsField field, @Nullable Object match, @Nullable CollateFunction collate) {
        List<WbsRecord> records = new ArrayList<>();

        String whereClaus = field.getFieldName() + " = ?";
        if (collate != null) whereClaus += " COLLATE " + collate;
        String query = getSelectQuery(whereClaus);

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setObject(1, match);

            if (debugMode) {
                database.getPlugin().logger.info("selectOnField: " + statement);
            }

            records = database.select(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return records;
    }

    @NotNull
    public List<WbsRecord> selectOnFields(Collection<WbsField> fields, Collection<?> matches) {
        if (fields.size() != matches.size()) {
            throw new IllegalArgumentException("Number of fields must match number of matches.");
        }

        if (fields.size() == 0) {
            throw new IllegalArgumentException("At least one field must be provided.");
        }

        List<WbsRecord> records = new ArrayList<>();

        String fieldsString = this.fields.stream()
                .map(WbsField::getFieldName)
                .collect(Collectors.joining(", "));

        String where = fields.stream()
                .map(field -> field.getFieldName() + " = ?")
                .collect(Collectors.joining(" AND "));

        String query = getSelectQuery(fieldsString, where);

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(query))
        {
            int i = 1;
            for (Object match : matches) {
                statement.setObject(i, match);
                i++;
            }

            if (debugMode) {
                database.getPlugin().logger.info("selectOnField: " + statement);
            }

            records = database.select(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return records;
    }

    /**
     * Inserts the given map of objects.
     * @param records The record to insert, with all fields populated.
     * @return True if the upsert was successful, false if any key was missing or no records were provided
     */
    public boolean upsert(List<WbsRecord> records) {
        if (records.isEmpty()) return false;

        String keyQuery = getPrimaryKeyQuery();

        String whereClause = "(" +
                records.stream()
                        .map(record -> keyQuery)
                        .collect(Collectors.joining(") OR ("))
                + ")";

        String query = getSelectQuery(whereClause);

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(query))
        {

            int paramIndex = 1;
            for (WbsRecord record : records) {
                for (WbsField primaryKey : getPrimaryKeys()) {
                    statement.setObject(paramIndex, record.getValue(primaryKey));
                    paramIndex++;
                }
            }

            if (debugMode) {
                database.getPlugin().logger.info("upsert (insert/update sort): " + statement);
            }

            ResultSet set = statement.executeQuery();

            List<WbsRecord> update = new LinkedList<>();
            List<WbsRecord> insert = new LinkedList<>();

            while (set.next()) {
                ResultSetMetaData metaData = set.getMetaData();

                Map<WbsField, Object> keyValues = new HashMap<>();
                for (int i = 1; i < metaData.getColumnCount() + 1; i++) {
                    String tableName = metaData.getTableName(i);
                    String fieldName = metaData.getColumnName(i).split(":")[0];

                    WbsField field = database.getField(tableName, fieldName);

                    if (field == null) {
                        throw new WbsDatabaseException("WbsField not defined on table \"" + tableName + "\" for field \"" + fieldName + "\"");
                    }

                    if (field.isPrimaryKey()) {
                        keyValues.put(field, set.getObject(i));
                    }
                }

                WbsRecord match = null;

                recordIter: for (WbsRecord record : records) {
                    for (WbsField primaryKey : getPrimaryKeys()) {
                        if (!Objects.equals(record.getValue(primaryKey), keyValues.get(primaryKey))) {
                            continue recordIter;
                        }
                    }

                    match = record;
                    break;
                }

                if (match == null) {
                    throw new WbsDatabaseException("Record outside provided set matched in primary key selection.");
                }

                update.add(match);
            }

            for (WbsRecord record : records) {
                if (!update.contains(record)) {
                    insert.add(record);
                }
            }

            boolean updateSucceeded = true;
            if (!update.isEmpty()) {
                updateSucceeded = update(update, connection, getPrimaryKeyQuery());
            }

            boolean insertSucceeded = true;
            if (!insert.isEmpty()) {
                insertSucceeded = insert(insert, connection);
            }

            return updateSucceeded && insertSucceeded;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (WbsDatabaseException e) {
            Logger logger = database.getPlugin().logger;
            e.forEach((sqlEx, exceptionQuery) -> {
                logger.info("Failed to upsert. Query: " + exceptionQuery);
                sqlEx.printStackTrace();
            });
        }
        return false;
    }

    /**
     * Inserts the given map of objects.
     * @param records The record to insert, with all fields populated.
     * @return True if the insert was successful, false if any key was missing or no records were provided
     */
    public boolean insert(List<WbsRecord> records) {
        if (records.isEmpty()) return false;

        try (Connection connection = database.getConnection()){
            return insert(records, connection);
        } catch (SQLException e) {
            database.getPlugin().logger.info("Failed to insert; connection error.");
            e.printStackTrace();
        } catch (WbsDatabaseException e) {
            Logger logger = database.getPlugin().logger;
            e.forEach((sqlEx, exceptionQuery) -> {
                logger.info("Failed to upsert. Query: " + exceptionQuery);
                sqlEx.printStackTrace();
            });
        }
        return false;
    }

    public boolean insert(List<WbsRecord> records, Connection connection) throws WbsDatabaseException {
        if (records.isEmpty()) return false;

        String query = getInsertStatement(records.size());
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            return runWithRecord(records, statement);
        } catch (SQLException e) {
            throw new WbsDatabaseException(e, query);
        }
    }

    public boolean update(List<WbsRecord> records) {
        return update(records, getPrimaryKeyQuery());
    }

    /**
     * Updates the given list of records using the provided whereClause.
     * @param records The records to update, with all fields populated.
     * @param whereClause The where condition to update on, with fields populated for each record individually,
     *                    in the same order as the fields are declared on the table. To use complex conditions
     *                    that use fields in another order, or more than once, perform a direct query.
     * @return True if the update was successful, false if any key was missing or no records were provided.
     */
    public boolean update(List<WbsRecord> records, String whereClause) {
        if (records.isEmpty()) return false;

        try (Connection connection = database.getConnection()){
            return update(records, connection, whereClause);
        } catch (SQLException e) {
            database.getPlugin().logger.info("Failed to update; connection error.");
            e.printStackTrace();
        } catch (WbsDatabaseException e) {
            Logger logger = database.getPlugin().logger;
            e.forEach((sqlEx, exceptionQuery) -> {
                logger.info("Failed to update. Query: " + exceptionQuery);
                sqlEx.printStackTrace();
            });
        }
        return false;
    }

    /**
     * Updates the given list of records using the provided whereClause. The given connection is not closed,
     * allowing for batching.
     * @param records The records to update, with all fields populated.
     * @param connection The connection to use for all queries performed.
     * @param whereClause The where condition to update on, with fields populated for each record individually,
     *                    in the same order as the fields are declared on the table. To use complex conditions
     *                    that use fields in another order, or more than once, perform a direct query.
     * @return True if the update was successful, false if any key was missing or no records were provided.
     */
    public boolean update(List<WbsRecord> records, Connection connection, String whereClause) throws WbsDatabaseException {
        if (records.isEmpty()) return false;
        WbsDatabaseException finalException = new WbsDatabaseException();

        boolean allSucceeded = true;
        for (WbsRecord record : records) {
            try {
                allSucceeded &= update(record, connection, whereClause);
            } catch (WbsDatabaseException e) {
                e.forEach(finalException::addQueryException);
            }
        }

        if (!finalException.getQueryExceptions().isEmpty()) {
            throw finalException;
        }

        return allSucceeded;
    }

    /**
     * Performs an update using a single record for the fields, using the given where clause.
     * The given connection is not closed, allowing for batching.
     * @param record The records to update, with all fields populated.
     * @param connection The connection to use for all queries performed.
     * @param whereClause The where condition to update on, with fields populated for each record individually,
     *                    in the same order as the fields are declared on the table. To use complex conditions
     *                    that use fields in another order, or more than once, perform a direct query.
     * @return True if the update was successful, false if any key was missing or no records were provided.
     */
    public boolean update(WbsRecord record, Connection connection, String whereClause) throws WbsDatabaseException {
        String query = getUpdateStatement(whereClause);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            int index = 1;
            ParameterMetaData metaData = statement.getParameterMetaData();

            while (index <= metaData.getParameterCount()) {
                for (WbsField field : fields) {
                    if (index > metaData.getParameterCount()) break;
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

            if (debugMode) {
                database.getPlugin().logger.info("update: " + statement);
            }

            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new WbsDatabaseException(e, query);
        }
    }

    private boolean runWithRecord(List<WbsRecord> records, PreparedStatement statement) throws SQLException {
        // Prepared statements aren't zero-indexed; they start at 1
        int index = 1;

        ParameterMetaData metaData = statement.getParameterMetaData();

        int recordIndex = 0;
        while (index <= metaData.getParameterCount() && recordIndex < records.size()) {
            WbsRecord record = records.get(recordIndex);
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
            recordIndex++;
        }

        if (debugMode) {
            database.getPlugin().logger.info("runWithRecord: " + statement);
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

    public String getPrimaryKeyQuery() {
        return getPrimaryKeys().stream()
                .map(key -> key.getFieldName() + " = ?")
                .collect(Collectors.joining(" AND "));
    }

    public List<WbsField> getPrimaryKeys() {
        return fields.stream().filter(WbsField::isPrimaryKey).collect(Collectors.toList());
    }

    public List<WbsField> getFields() {
        return new ArrayList<>(fields);
    }

    public boolean inDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
}
