package com.flipfit.dao;

import com.flipfit.util.DBUtil;
import com.flipfit.exceptions.DbConnectionException;

import java.sql.*;

public class WaitlistDAO {

    private static volatile WaitlistDAO instance = null;

    private WaitlistDAO() {
    }

    public static WaitlistDAO getInstance() {
        if (instance == null) {
            synchronized (WaitlistDAO.class) {
                if (instance == null) instance = new WaitlistDAO();
            }
        }
        return instance;
    }

    private Connection getConnection() throws SQLException {
        return DBUtil.getConnection();
    }

    public void addToWaitlist(int slotId, int userId) throws DbConnectionException {
        String query = "INSERT INTO waitlist (slotId, userId) VALUES (?, ?)";
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, slotId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
            // Optional: You can keep System.out for debugging, or remove for production
            System.out.println("[DB] User " + userId + " added to waitlist for slot " + slotId);
        } catch (SQLException e) {
            throw new DbConnectionException("Error adding user " + userId + " to waitlist", e);
        }
    }

    public Integer removeFromWaitlist(int slotId) throws DbConnectionException {
        String selectQuery = "SELECT waitlistId, userId FROM waitlist WHERE slotId = ? ORDER BY waitlistId ASC LIMIT 1 FOR UPDATE";
        String deleteQuery = "DELETE FROM waitlist WHERE waitlistId = ?";

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Start Transaction

            int userId = -1;
            boolean found = false;

            try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
                selectStmt.setInt(1, slotId);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        int waitlistId = rs.getInt("waitlistId");
                        userId = rs.getInt("userId");
                        found = true;

                        // Delete the entry
                        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                            deleteStmt.setInt(1, waitlistId);
                            deleteStmt.executeUpdate();
                        }
                    }
                }
            }

            if (found) {
                conn.commit();
                return userId;
            } else {
                conn.rollback();
                return null;
            }

        } catch (SQLException e) {
            // Handle Rollback
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // Log this, don't throw it, or it masks the original error
                    System.err.println("Error during rollback: " + ex.getMessage());
                }
            }
            throw new DbConnectionException("Error removing user from waitlist for slot " + slotId, e);
        } finally {
            // Reset AutoCommit
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close(); // Important: Close connection manually since we didn't use try-with-resources for 'conn'
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    public boolean hasWaitlistedCustomers(int slotId) throws DbConnectionException {
        String query = "SELECT COUNT(*) FROM waitlist WHERE slotId = ?";
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, slotId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DbConnectionException("Error checking waitlist status", e);
        }
        return false;
    }

    public int getWaitlistSize(int slotId) throws DbConnectionException {
        String query = "SELECT COUNT(*) FROM waitlist WHERE slotId = ?";
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, slotId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DbConnectionException("Error fetching waitlist size", e);
        }
        return 0;
    }
}