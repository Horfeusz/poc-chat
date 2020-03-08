package be.chat.db;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DB {

    private static final String CONNECTION_STRING = "jdbc:sqlite:C:/Projekty/DKV/POC/users.db";

    private static Logger log = Logger.getLogger(DB.class.getCanonicalName());

    private static java.sql.Connection conn = null;

    private static void init() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(CONNECTION_STRING);
            log.info("Connecting to database");
        } catch (Exception e) {
            log.throwing(DB.class.getCanonicalName(), "init", e);
            throw new SQLException(e);
        }
    }

    public static java.sql.Connection get() throws SQLException {
        if (conn == null || conn.isClosed()) {
            init();
        }
        return conn;
    }

    public static void close() throws SQLException {
        if (conn != null) {
            if (!conn.isClosed())
                conn.close();
        }
    }
}
