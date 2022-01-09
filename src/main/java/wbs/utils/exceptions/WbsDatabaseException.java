package wbs.utils.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class WbsDatabaseException extends RuntimeException {

    @NotNull
    private final List<QueryExceptionEntry> queryExceptions = new LinkedList<>();

    public WbsDatabaseException() {}

    public WbsDatabaseException(String message) {
        super(message);
    }

    public WbsDatabaseException(@NotNull SQLException e, @NotNull String query) {
        queryExceptions.add(new QueryExceptionEntry(e, query));
    }

    public WbsDatabaseException addQueryException(@NotNull SQLException e, @NotNull String query) {
        queryExceptions.add(new QueryExceptionEntry(e, query));
        return this;
    }

    public List<SQLException> getAllWrapped() {
        return queryExceptions.stream().map(QueryExceptionEntry::getException).collect(Collectors.toList());
    }

    public @Nullable String getQuery() {
        return queryExceptions.get(0).getQuery();
    }

    public List<String> getQueries() {
        return queryExceptions.stream().map(QueryExceptionEntry::getQuery).collect(Collectors.toList());
    }

    @NotNull
    public Collection<QueryExceptionEntry> getQueryExceptions() {
        return Collections.unmodifiableCollection(queryExceptions);
    }

    public WbsDatabaseException forEach(BiConsumer<SQLException, String> consumer) {
        queryExceptions.forEach(entry -> consumer.accept(entry.getException(), entry.getQuery()));
        return this;
    }

    public static class QueryExceptionEntry {
        private final SQLException e;
        private final String query;

        public QueryExceptionEntry(SQLException e, String query) {
            this.e = e;
            this.query = query;
        }

        public SQLException getException() {
            return e;
        }

        public String getQuery() {
            return query;
        }
    }
}
