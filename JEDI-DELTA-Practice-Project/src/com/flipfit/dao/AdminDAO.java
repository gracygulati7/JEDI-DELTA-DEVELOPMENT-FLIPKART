package com.flipfit.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminDAO {

    private static AdminDAO instance;

    // ---- DATABASE CONFIG ----
    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String SCHEMA = "FlipFit_schema";
    private static final String USER = "root";
    private static final String PASSWORD = "Lochan@1999";

    private static final String URL =
    	    "jdbc:mysql://localhost:3306/FlipFit_schema" +
    	    "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";


    private AdminDAO() {}

    public static AdminDAO getInstance() {
        if (instance == null) {
            instance = new AdminDAO();
        }
        return instance;
    }

    // ---- DB CONNECTION ----
    private Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ---- ADMIN AUTHENTICATION ----
    public boolean login(String email, String password) {

        String sql =
            "SELECT u.user_id " +
            "FROM users u " +
            "JOIN admins a ON u.user_id = a.admin_id " +
            "WHERE u.full_name = ? AND u.password = ? AND u.role = 'ADMIN'";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            return rs.next();   // true → valid admin

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
