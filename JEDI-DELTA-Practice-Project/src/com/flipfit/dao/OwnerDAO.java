package com.flipfit.dao;

import com.flipfit.bean.FlipFitGymOwner;
import com.flipfit.util.DBUtil;
import com.flipfit.exceptions.DbConnectionException;
import com.flipfit.exceptions.UserNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OwnerDAO {

    private static OwnerDAO instance = null;

    private OwnerDAO() {}

    public static OwnerDAO getInstance() {
        if (instance == null) {
            synchronized (OwnerDAO.class) {
                if (instance == null) instance = new OwnerDAO();
            }
        }
        return instance;
    }

    private Connection getConnection() throws SQLException {
        return DBUtil.getConnection();
    }

    public void addOwnerDetails(int ownerId, String pan, String aadhaar, String gstin) throws DbConnectionException {
        String sql = "INSERT INTO Owner (owner_id, pan, aadhaar, gstin, is_approved) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ownerId);
            pstmt.setString(2, pan);
            pstmt.setString(3, aadhaar); 
            pstmt.setString(4, gstin);
            pstmt.setInt(5, 0);

            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new DbConnectionException("Error adding owner details", e);
        }
    }

    public FlipFitGymOwner getOwnerById(int id) throws DbConnectionException, UserNotFoundException {
        String sql = "SELECT u.full_name, o.owner_id, o.pan, o.aadhaar, o.gstin " +
                "FROM Owner o " +
                "JOIN users u ON o.owner_id = u.user_id " +
                "WHERE o.owner_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new FlipFitGymOwner(
                            rs.getInt("owner_id"),
                            rs.getString("full_name"),
                            rs.getString("pan"),
                            rs.getString("aadhaar"),
                            rs.getString("gstin")
                    );
                } else {
                    throw new UserNotFoundException("Owner not found with ID: " + id);
                }
            }
        } catch (SQLException e) {
            throw new DbConnectionException("Error fetching owner by ID", e);
        }
    }

    public FlipFitGymOwner getOwnerByName(String name) throws DbConnectionException, UserNotFoundException {
        String sql = "SELECT u.full_name, o.owner_id, o.pan, o.aadhaar, o.gstin " +
                "FROM Owner o " +
                "JOIN users u ON o.owner_id = u.user_id " +
                "WHERE u.full_name = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new FlipFitGymOwner(
                            rs.getInt("owner_id"),
                            rs.getString("full_name"),
                            rs.getString("pan"),
                            rs.getString("aadhaar"),
                            rs.getString("gstin")
                    );
                } else {
                     throw new UserNotFoundException("Owner not found with name: " + name);
                }
            }
        } catch (SQLException e) {
            throw new DbConnectionException("Error fetching owner by name", e);
        }
    }

    public void addOwner(String name) throws DbConnectionException {
        String sql = "INSERT INTO users (full_name, role) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, "OWNER");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DbConnectionException("Error adding owner user", e);
        }
    }
    
    public void addOwner(String name, String email, String password) throws DbConnectionException {
        String sql = "INSERT INTO users (full_name, email, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setString(4, "OWNER");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DbConnectionException("Error adding owner credentials", e);
        }
    }

    public FlipFitGymOwner getOrCreateOwnerByName(String name) throws DbConnectionException {
        try {
            return getOwnerByName(name);
        } catch (UserNotFoundException e) {
            addOwner(name);
            try {
                return getOwnerByName(name);
            } catch (UserNotFoundException ex) {
                // Should technically never happen right after adding
                throw new DbConnectionException("Error verifying new owner creation", ex);
            }
        }
    }

    public Collection<FlipFitGymOwner> getAllOwners() throws DbConnectionException {
        List<FlipFitGymOwner> owners = new ArrayList<>();
        String sql = "SELECT u.full_name, o.owner_id, o.pan, o.aadhaar, o.gstin FROM Owner o JOIN users u ON o.owner_id = u.user_id";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                owners.add(new FlipFitGymOwner(
                        rs.getInt("owner_id"),
                        rs.getString("full_name"),
                        rs.getString("pan"),
                        rs.getString("aadhaar"),
                        rs.getString("gstin")
                ));
            }
        } catch (SQLException e) {
            throw new DbConnectionException("Error fetching all owners", e);
        }
        return owners;
    }

    public int getNextOwnerId() throws DbConnectionException {
        String sql = "SELECT MAX(owner_id) FROM Owner";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int maxId = rs.getInt(1);
                if (rs.wasNull()) {
                    return 1; 
                }
                return maxId + 1;
            }
        } catch (SQLException e) {
            throw new DbConnectionException("Error generating next owner ID", e);
        }
        return 1;
    }
}