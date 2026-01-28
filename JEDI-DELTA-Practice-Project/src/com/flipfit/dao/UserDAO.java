package com.flipfit.dao;

import com.flipfit.util.DBUtil;
import com.flipfit.exceptions.DbConnectionException;
import java.sql.*;

public class UserDAO {
    private static UserDAO instance = null;

    private UserDAO() {}

    public static UserDAO getInstance() {
        if (instance == null) {
            synchronized (UserDAO.class) {
                if (instance == null) instance = new UserDAO();
            }
        }
        return instance;
    }

    public void registerUser(int userId, String name, String email, String password, String role) throws DbConnectionException {
        String sql = "INSERT INTO users (user_id, full_name, email, password, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, name);
            ps.setString(3, email);
            ps.setString(4, password);
            ps.setString(5, role);
            ps.executeUpdate();
        } catch (SQLException e) {
            // Check for Duplicate Entry error (SQLState 23000 is standard for integrity constraint violation)
            if ("23000".equals(e.getSQLState())) {
                System.out.println("User " + name + " already exists. Skipping.");
            } else {
                // For all other errors (Connection failed, syntax error), throw our custom exception
                throw new DbConnectionException("Error registering user: " + name, e);
            }
        }
    }
}