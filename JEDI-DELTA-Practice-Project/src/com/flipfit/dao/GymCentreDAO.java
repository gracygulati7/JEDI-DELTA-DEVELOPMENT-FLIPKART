package com.flipfit.dao;

import com.flipfit.bean.FlipFitGymCenter;
import com.flipfit.util.DBUtil;
import com.flipfit.exceptions.DbConnectionException;
import com.flipfit.exceptions.CentreNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GymCentreDAO {

    private static GymCentreDAO instance = null;

    private GymCentreDAO() {}

    public static GymCentreDAO getInstance() {
        if (instance == null) {
            synchronized (GymCentreDAO.class) {
                if (instance == null) instance = new GymCentreDAO();
            }
        }
        return instance;
    }

    private Connection getConnection() throws SQLException {
        return DBUtil.getConnection();
    }

    public void addGymCentre(FlipFitGymCenter gymCentre) throws DbConnectionException {
        String sql = "INSERT INTO GymCentreTable (centre_id, owner_id, gym_name, city, state, pincode, capacity, is_approved) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, gymCentre.getCenterId());
            pstmt.setInt(2, gymCentre.getOwnerId());
            pstmt.setString(3, gymCentre.getGymName());
            pstmt.setString(4, gymCentre.getCity());
            pstmt.setString(5, gymCentre.getState());
            pstmt.setInt(6, gymCentre.getPincode());
            pstmt.setInt(7, gymCentre.getCapacity());
            pstmt.setBoolean(8, gymCentre.isApproved());

            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new DbConnectionException("Error adding gym centre", e);
        }
    }

    public List<FlipFitGymCenter> getAllCentres() throws DbConnectionException {
        List<FlipFitGymCenter> centers = new ArrayList<>();
        String sql = "SELECT * FROM GymCentreTable";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                FlipFitGymCenter center = new FlipFitGymCenter(
                    rs.getInt("centre_id"),
                    rs.getString("gym_name"),
                    rs.getString("city"),
                    rs.getString("state"),
                    rs.getInt("pincode"),
                    rs.getInt("capacity")
                );
                center.setOwnerId(rs.getInt("owner_id"));
                centers.add(center);
            }
        } catch (SQLException e) {
            throw new DbConnectionException("Error fetching all centres", e);
        }
        return centers;
    }

    public FlipFitGymCenter getGymCentreById(int centreId) throws DbConnectionException, CentreNotFoundException {
        String sql = "SELECT * FROM GymCentreTable WHERE centre_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, centreId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    FlipFitGymCenter center = new FlipFitGymCenter(
                        rs.getInt("centre_id"),
                        rs.getString("gym_name"),
                        rs.getString("city"),
                        rs.getString("state"),
                        rs.getInt("pincode"),
                        rs.getInt("capacity")
                    );
                    center.setOwnerId(rs.getInt("owner_id"));
                    return center;
                } else {
                    // String.valueOf because your constructor expects a String ID
                    throw new CentreNotFoundException(String.valueOf(centreId));
                }
            }
        } catch (SQLException e) {
            throw new DbConnectionException("Error fetching gym centre by ID", e);
        }
    }

    public void approveCenter(int centerId) throws DbConnectionException, CentreNotFoundException {
        String sql = "UPDATE GymCentreTable SET is_approved = 1 WHERE centre_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, centerId);
            int affected = pstmt.executeUpdate();
            if (affected == 0) {
                 throw new CentreNotFoundException(String.valueOf(centerId));
            }
        } catch (SQLException e) {
            throw new DbConnectionException("Error approving centre", e);
        }
    }

    public void deleteGymCentre(int centreId) throws DbConnectionException, CentreNotFoundException {
        String sql = "DELETE FROM GymCentreTable WHERE centre_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, centreId);
            int affected = pstmt.executeUpdate();
            if (affected == 0) {
                 throw new CentreNotFoundException(String.valueOf(centreId));
            }
        } catch (SQLException e) {
            throw new DbConnectionException("Error deleting gym centre", e);
        }
    }

    public int getNextCentreId() throws DbConnectionException {
        String sql = "SELECT MAX(centre_id) FROM GymCentreTable";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int max = rs.getInt(1);
                if (rs.wasNull() || max == 0) {
                    return 1;
                }
                return max + 1;
            }
        } catch (SQLException e) {
            throw new DbConnectionException("Error generating next centre ID", e);
        }
        return 1;
    }

    public boolean centreIdExists(int centreId) throws DbConnectionException {
        String sql = "SELECT 1 FROM GymCentreTable WHERE centre_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, centreId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
             throw new DbConnectionException("Error checking centre existence", e);
        }
    }
}