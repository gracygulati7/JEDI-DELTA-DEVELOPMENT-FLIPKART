package com.flipfit.dao;

import com.flipfit.bean.FlipFitGymOwner;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OwnerDAO {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/flipfit_db";
    private static final String USER = "root";
    private static final String PASS = "password";

    private static OwnerDAO instance = null;

    private OwnerDAO() {}

    public static OwnerDAO getInstance() {
        if (instance == null) {
            instance = new OwnerDAO();
        }
        return instance;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public void addOwnerDetails(int ownerId, String pan, String aadhaar, String gstin) {
        String sql = "INSERT INTO Owner (ownerid, pan, aadhar, gstin, isApproved) VALUES (?, ?, ?, ?, ?)";

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
            System.err.println("Error adding owner details: " + e.getMessage());
        }
    }


    public FlipFitGymOwner getOwnerById(int id) {
        String sql = "SELECT u.name, o.ownerid, o.pan, o.aadhar, o.gstin " +
                "FROM Owner o " +
                "JOIN User u ON o.ownerid = u.ownerid " +
                "WHERE o.ownerid = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new FlipFitGymOwner(
                            rs.getInt("ownerid"),
                            rs.getString("name"),
                            rs.getString("pan"),
                            rs.getString("aadhar"),
                            rs.getString("gstin")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public FlipFitGymOwner getOwnerByName(String name) {
        String sql = "SELECT u.name, o.ownerid, o.pan, o.aadhar, o.gstin " +
                "FROM Owner o " +
                "JOIN User u ON o.ownerid = u.ownerid " +
                "WHERE u.name = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new FlipFitGymOwner(
                            rs.getInt("ownerid"),
                            rs.getString("name"),
                            rs.getString("pan"),
                            rs.getString("aadhar"),
                            rs.getString("gstin")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Collection<FlipFitGymOwner> getAllOwners() {
        List<FlipFitGymOwner> owners = new ArrayList<>();
        String sql = "SELECT u.name, o.ownerid, o.pan, o.aadhar, o.gstin FROM Owner o JOIN User u ON o.ownerid = u.ownerid";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                owners.add(new FlipFitGymOwner(
                        rs.getInt("ownerid"),
                        rs.getString("name"),
                        rs.getString("pan"),
                        rs.getString("aadhar"),
                        rs.getString("gstin")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return owners;
    }

    public int getNextOwnerId() {
        String sql = "SELECT MAX(ownerid) FROM Owner";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int maxId = rs.getInt(1);
                return (maxId == 0) ? 0 : maxId + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}