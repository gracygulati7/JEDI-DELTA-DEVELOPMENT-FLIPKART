package com.flipfit.dao;

import com.flipfit.util.DBUtil;

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

    /**
     * Adds a user to the waitlist for a slot.
     */
    public void addToWaitlist(int slotId, int userId) {
        String query = "INSERT INTO waitlist (slot_id, user_id) VALUES (?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, slotId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
            System.out.println("[DB] User " + userId + " added to waitlist for slot " + slotId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes the oldest waitlisted user for a slot and returns their userId.
     * This is done inside a transaction and deletes by waitlistId to avoid races.
     * Returns null if there is no waitlisted user.
     */
    public Integer removeFromWaitlist(int slotId) {
        String selectQuery = "SELECT waitlist_id, user_id FROM waitlist WHERE slot_id = ? ORDER BY waitlist_id ASC LIMIT 1 FOR UPDATE";
        String deleteQuery = "DELETE FROM waitlist WHERE waitlist_id = ?";

        try (Connection conn = getConnection()) {
            try {
                conn.setAutoCommit(false);

                try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
                    selectStmt.setInt(1, slotId);
                    try (ResultSet rs = selectStmt.executeQuery()) {
                        if (rs.next()) {
                            int waitlistId = rs.getInt("waitlist_id");
                            int userId = rs.getInt("user_id");

                            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                                deleteStmt.setInt(1, waitlistId);
                                deleteStmt.executeUpdate();
                            }

                            conn.commit();
                            return userId;
                        } else {
                            conn.rollback();
                            return null;
                        }
                    }
                }
            } catch (SQLException ex) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
                ex.printStackTrace();
                throw new RuntimeException(ex);
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns true if there is at least one waitlisted customer for the slot.
     */
    public boolean hasWaitlistedCustomers(int slotId) {
        String query = "SELECT COUNT(*) FROM waitlist WHERE slot_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, slotId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return false;
    }

    /**
     * Returns the size of the waitlist for a slot.
     */
    public int getWaitlistSize(int slotId) {
        String query = "SELECT COUNT(*) FROM waitlist WHERE slot_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, slotId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return 0;
    }
}