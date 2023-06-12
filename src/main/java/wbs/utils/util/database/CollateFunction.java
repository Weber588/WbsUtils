package wbs.utils.util.database;

/**
 * See <a href="https://www.w3resource.com/sqlite/sqlite-collating-function-or-sequence.php">
 *     SQLite Collating Sequences</a>
 */
public enum CollateFunction {
    /**
     * It is almost same as binary, except the 26 upper case characters of ASCII are
     * folded to their lower case equivalents before the comparison is performed.
     * @see <a href="https://www.w3resource.com/sqlite/sqlite-collating-function-or-sequence.php">
     *     SQLite Collating Sequences</a>
     */
    NOCASE,
    /**
     * Compares string data using memcmp(), regardless of text encoding.
     * @see <a href="https://www.w3resource.com/sqlite/sqlite-collating-function-or-sequence.php">
     *     SQLite Collating Sequences</a>
     */
    BINARY,
    /**
     * The same as binary, except that trailing space characters, are ignored.
     * @see <a href="https://www.w3resource.com/sqlite/sqlite-collating-function-or-sequence.php">
     *     SQLite Collating Sequences</a>
     */
    RTRIM
}
