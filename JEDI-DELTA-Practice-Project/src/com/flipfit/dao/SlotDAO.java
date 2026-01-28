package com.flipfit.dao;

import com.flipfit.bean.Slot;
import com.flipfit.util.DBUtil;
import com.flipfit.exceptions.DbConnectionException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SlotDAO {

    private static SlotDAO instance = null;

    private SlotDAO() {}

    public static SlotDAO getInstance() {
        if (instance == null) {
            synchronized (SlotDAO.class) {
                if (instance == null) instance = new SlotDAO();
            }
        }
        return instance;
    }

    private Connection getConnection() throws SQLException {
        return DBUtil.getConnection();
    }

    public void addSlot(Slot slot) throws DbConnectionException {
        String query = "INSERT INTO slots (slot_id, centre_id, slot_date, start_time, end_time, total_seats, available_seats) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, slot.getSlotId());
            stmt.setInt(2, slot.getCenterId());
            stmt.setDate(3, Date.valueOf(slot.getDate()));
            stmt.setTime(4, Time.valueOf(slot.getStartTime()));
            stmt.setTime(5, Time.valueOf(slot.getEndTime())); 
            stmt.setInt(6, slot.getTotalSeats());
            stmt.setInt(7, slot.getSeatsAvailable());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DbConnectionException("Error adding slot " + slot.getSlotId(), e);
        }
    }

    public List<Slot> getSlotsByCenterId(int centerId) throws DbConnectionException {
        List<Slot> centerSlots = new ArrayList<>();
        String query = "SELECT * FROM slots WHERE centre_id = ?";
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, centerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    centerSlots.add(mapResultSetToSlot(rs));
                }
            }
        } catch (SQLException e) {
            throw new DbConnectionException("Error fetching slots for center " + centerId, e);
        }
        return centerSlots;
    }

    public Slot getSlotById(int slotId) throws DbConnectionException {
        String query = "SELECT * FROM slots WHERE slot_id = ?";
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, slotId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSlot(rs);
                }
            }
        } catch (SQLException e) {
            throw new DbConnectionException("Error fetching slot by ID " + slotId, e);
        }
        return null;
    }

    public Slot getSlotById(int slotId, int centerId) throws DbConnectionException {
        String query = "SELECT * FROM slots WHERE slot_id = ? AND centre_id = ?";
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, slotId);
            stmt.setInt(2, centerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSlot(rs);
                }
            }
        } catch (SQLException e) {
            throw new DbConnectionException("Error fetching slot by ID and Center", e);
        }
        return null;
    }
    
    public int getNextSlotId() throws DbConnectionException {
        String sql = "SELECT MAX(slot_id) FROM slots";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int maxId = rs.getInt(1);
                return (maxId == 0) ? 1 : maxId + 1;
            }
        } catch (SQLException e) {
            throw new DbConnectionException("Error generating next slot ID", e);
        }
        return 1; 
    }

    public List<Slot> getAllSlots() throws DbConnectionException {
        List<Slot> slots = new ArrayList<>();
        String query = "SELECT * FROM slots";
        try (Connection conn = getConnection(); 
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                slots.add(mapResultSetToSlot(rs));
            }
        } catch (SQLException e) {
            throw new DbConnectionException("Error fetching all slots", e);
        }
        return slots;
    }
    
    public List<Slot> getAvailableSlotsByDateAndCenter(int centerId, LocalDate date) throws DbConnectionException {
        List<Slot> availableSlots = new ArrayList<>();
        String query = "SELECT * FROM slots WHERE centre_id = ? AND slot_date = ? AND available_seats > 0 AND (slot_date > CURRENT_DATE OR (slot_date = CURRENT_DATE AND start_time > CURRENT_TIME))";
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, centerId);
            stmt.setDate(2, Date.valueOf(date));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    availableSlots.add(mapResultSetToSlot(rs));
                }
            }
        } catch (SQLException e) {
            throw new DbConnectionException("Error fetching available slots", e);
        }
        return availableSlots;
    }

    public List<Slot> getFullSlotsByDateAndCenter(int centerId, LocalDate date) throws DbConnectionException {
        List<Slot> fullSlots = new ArrayList<>();
        String query = "SELECT * FROM slots WHERE centre_id = ? AND slot_date = ? AND available_seats = 0";
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, centerId);
            stmt.setDate(2, Date.valueOf(date));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    fullSlots.add(mapResultSetToSlot(rs));
                }
            }
        } catch (SQLException e) {
            throw new DbConnectionException("Error fetching full slots", e);
        }
        return fullSlots;
    }

    public List<Slot> getExpiredSlots() throws DbConnectionException {
        List<Slot> expiredSlots = new ArrayList<>();
        String query = "SELECT * FROM slots WHERE slot_date < CURRENT_DATE OR (slot_date = CURRENT_DATE AND start_time < CURRENT_TIME)";
        try (Connection conn = getConnection(); 
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                expiredSlots.add(mapResultSetToSlot(rs));
            }
        } catch (SQLException e) {
            throw new DbConnectionException("Error fetching expired slots", e);
        }
        return expiredSlots;
    }

    public List<Slot> getSlotsByDateRange(int centerId, LocalDate startDate, LocalDate endDate) throws DbConnectionException {
        List<Slot> rangeSlots = new ArrayList<>();
        String query = "SELECT * FROM slots WHERE centre_id = ? AND slot_date BETWEEN ? AND ?";
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, centerId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rangeSlots.add(mapResultSetToSlot(rs));
                }
            }
        } catch (SQLException e) {
            throw new DbConnectionException("Error fetching slots by date range", e);
        }
        return rangeSlots;
    }

    private Slot mapResultSetToSlot(ResultSet rs) throws SQLException {
        Slot slot = new Slot();
        slot.setSlotId(rs.getInt("slot_id"));
        slot.setCenterId(rs.getInt("centre_id"));
        slot.setDate(rs.getDate("slot_date").toLocalDate());
        slot.setStartTime(String.valueOf(rs.getTime("start_time").toLocalTime()));
        // Note: Assuming setEndTime() exists in your bean
        // slot.setEndTime(String.valueOf(rs.getTime("end_time").toLocalTime())); 
        slot.setTotalSeats(rs.getInt("total_seats"));
        slot.setSeatsAvailable(rs.getInt("available_seats"));
        return slot;
    }
}