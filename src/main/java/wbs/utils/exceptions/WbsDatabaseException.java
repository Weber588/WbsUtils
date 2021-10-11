package wbs.utils.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

public class WbsDatabaseException extends RuntimeException {

    @Nullable
    private SQLException wrapped;
    @Nullable
    private String query;

    public WbsDatabaseException() {}

    public WbsDatabaseException(String message) {
        super(message);
    }

    public WbsDatabaseException(@NotNull SQLException e) {
        wrapped = e;
    }

    public WbsDatabaseException(@NotNull SQLException e, @NotNull String query) {
        this.query = query;
        wrapped = e;
    }

    public @Nullable SQLException getWrapped() {
        return wrapped;
    }

    public @Nullable String getQuery() {
        return query;
    }
}
