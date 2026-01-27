package com.flipfit.dao;

import com.flipfit.bean.FlipFitGymOwner;
import com.flipfit.util.DBUtil;

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

    public void addOwnerDetails(int ownerId, String pan, String aadhaar, String gstin) {
        // FIX: Changed ownerid to owner_id and isApproved to is_approved
        String sql = "INSERT INTO Owner (owner_id, pan, aadhaar, gstin, is_approved) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ownerId);
            pstmt.setString(2, pan);
            pstmt.setString(3, aadhaar); 
            pstmt.setString(4, gstin);
            pstmt.setInt(5, 0);

            pstmt.executeUpdate();
            System.out.println("Owner professional details added successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public FlipFitGymOwner getOwnerById(int id) {
        // FIX: Updated column names to owner_id and aadhaar; Updated table name to users
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
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return null;
    }

    public FlipFitGymOwner getOwnerByName(String name) {
        // FIX: Updated column names to match your users table (full_name, user_id)
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
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return null;
    }

    public void addOwner(String name) {
        // FIX: Updated to users table and full_name column
        String sql = "INSERT INTO users (full_name, role) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, "OWNER");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public void addOwner(String name, String email, String password) {
        // FIX: Include email and password in the query
        String sql = "INSERT INTO users (full_name, email, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setString(4, "OWNER");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public FlipFitGymOwner getOrCreateOwnerByName(String name) {
        FlipFitGymOwner owner = getOwnerByName(name);
        if (owner != null) return owner;
        addOwner(name);
        return getOwnerByName(name);
    }

    public Collection<FlipFitGymOwner> getAllOwners() {
        List<FlipFitGymOwner> owners = new ArrayList<>();
        // FIX: Updated Join and labels
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
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return owners;
    }

    public int getNextOwnerId() {
        // FIX: Changed ownerid to owner_id
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
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return 1;
    }
}