package com.flipfit.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.flipfit.util.DBUtil;
import com.flipfit.constants.SQLConstants;
import com.flipfit.exceptions.DbConnectionException;
import com.flipfit.exceptions.WrongCredentialsException;

public class AdminDAO {

    private static AdminDAO instance;

    private AdminDAO() {}

    public static AdminDAO getInstance() {
        if (instance == null) {
            instance = new AdminDAO();
        }
        return instance;
    }

    // ---- ADMIN AUTHENTICATION ----
    // Changed return type to boolean, but added throws for specific errors
    // You can also change return type to 'void' if you rely purely on exceptions for flow control
    public boolean login(String username, String password) throws DbConnectionException, WrongCredentialsException {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQLConstants.ADMIN_LOGIN_QUERY)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true; // Valid admin
                } else {
                    throw new WrongCredentialsException();
                }
            }
        } catch (SQLException e) {
            throw new DbConnectionException("Database error during Admin login", e);
        }
    }
}