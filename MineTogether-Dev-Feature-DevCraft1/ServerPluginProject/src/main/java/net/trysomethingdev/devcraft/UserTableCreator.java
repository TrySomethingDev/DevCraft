package net.trysomethingdev.devcraft;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class UserTableCreator {
    public static void createTable() {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY," +
                    "username TEXT NOT NULL," +
                    "email TEXT," +
                    "level INTEGER" +
                    ")";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}