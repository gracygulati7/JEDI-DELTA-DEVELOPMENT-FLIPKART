package com.flipfit.dao;

import com.flipfit.bean.FlipFitGymCenter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GymCentreDAO {

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/flipfit_db";
    private static final String USER = "root";
    private static final String PASS = "password";

    private static GymCentreDAO instance = null;

    private GymCentreDAO() {}

    public static GymCentreDAO getInstance() {
        if (instance == null) {
            instance = new GymCentreDAO();
        }
        return instance;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public void addGymCentre(FlipFitGymCenter gymCentre) {
        String sql = "INSERT INTO GymCentreTable (centreid, ownerid, gymName, city, state, pincode, isApproved,) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, gymCentre.getCenterId());
            pstmt.setInt(2, gymCentre.getOwnerId());
            pstmt.setString(3, gymCentre.getgymName());
            pstmt.setString(4, gymCentre.getCity());
            pstmt.setString(5, gymCentre.getState());
            pstmt.setInt(6, gymCentre.getPincode());
            pstmt.setInt(7, 0);

            pstmt.executeUpdate();
            System.out.println("Gym Center added to database successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<FlipFitGymCenter> getAllCentres() {
        List<FlipFitGymCenter> centers = new ArrayList<>();
        String sql = "SELECT * FROM GymCentreTable";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                FlipFitGymCenter center = new FlipFitGymCenter(
                        rs.getInt("centreid"),
                        rs.getInt("ownerid"),
                        rs.getString("gymName"),
                        rs.getString("city"),
                        rs.getString("state"),
                        rs.getInt("pincode"),
                );
                centers.add(center);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return centers;
    }


    public FlipFitGymCenter getGymCentreById(int centreId) {
        String sql = "SELECT * FROM GymCentreTable WHERE centreid = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, centreId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new FlipFitGymCenter(
                            rs.getInt("centreid"),
                            rs.getInt("ownerid"),
                            rs.getString("gymName"),
                            rs.getString("city"),
                            rs.getString("state"),
                            rs.getInt("pincode"),
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void approveCenter(int centerId) {
        String sql = "UPDATE GymCentreTable SET isApproved = 1 WHERE centreid = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, centerId);
            pstmt.executeUpdate();
            System.out.println("Center approved successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void deleteGymCentre(int centreId) {
        String sql = "DELETE FROM GymCentreTable WHERE centreid = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, centreId);
            pstmt.executeUpdate();
            System.out.println("Gym Center deleted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getNextCentreId() {
        String sql = "SELECT MAX(centreid) FROM GymCentreTable";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean centreIdExists(int centreId) {
        String sql = "SELECT 1 FROM GymCentreTable WHERE centreid = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, centreId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}