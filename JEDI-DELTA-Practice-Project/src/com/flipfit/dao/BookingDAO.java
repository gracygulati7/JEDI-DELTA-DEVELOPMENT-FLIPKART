package com.flipfit.dao;

import com.flipfit.bean.Booking;
import com.flipfit.util.DBUtil;
import com.flipfit.exceptions.BookingFailedException;
import com.flipfit.exceptions.DbConnectionException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    private static BookingDAO instance = null;

    private BookingDAO() {}

    public static BookingDAO getInstance() {
        if (instance == null) {
            instance = new BookingDAO();
        }
        return instance;
    }

    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(rs.getInt("booking_id"));
        booking.setUserId(rs.getInt("user_id"));
        booking.setSlotId(rs.getInt("slot_id"));
        booking.setStatus(Booking.BookingStatus.valueOf(rs.getString("status")));
        booking.setDeleted(rs.getBoolean("is_deleted"));
        return booking;
    }

    // Changed to throw DbConnectionException
    public Booking createBooking(int userId, int slotId) throws DbConnectionException {
        String sql = "INSERT INTO booking (user_id, slot_id, status, is_deleted) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, userId);
            ps.setInt(2, slotId);
            ps.setString(3, Booking.BookingStatus.CONFIRMED.name());
            ps.setBoolean(4, false);

            int affected = ps.executeUpdate();
            if (affected == 0) {
                // BookingFailedException is a RuntimeException per your file, so no 'throws' needed in signature,
                // but good to document.
                throw new BookingFailedException("Failed to create booking: No rows affected.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return getBookingById(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DbConnectionException("Database error creating booking", e);
        }
        return null;
    }

    public Booking createWaitlistingBooking(int userId, int slotId) throws DbConnectionException {
        String sql = "INSERT INTO booking (user_id, slot_id, status, is_deleted) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, userId);
            ps.setInt(2, slotId);
            ps.setString(3, Booking.BookingStatus.WAITLISTED.name());
            ps.setBoolean(4, false);

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return getBookingById(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DbConnectionException("Database error creating waitlist booking", e);
        }
        return null;
    }

    public List<Booking> getBookingsByUserId(int userId) throws DbConnectionException {
        List<Booking> userBookings = new ArrayList<>();
        String sql = "SELECT * FROM booking WHERE user_id = ? AND is_deleted = false";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    userBookings.add(mapResultSetToBooking(rs));
                }
            }
        } catch (SQLException e) {
            throw new DbConnectionException("Error fetching bookings for user " + userId, e);
        }
        return userBookings;
    }

    public Booking getBookingById(int bookingId) throws DbConnectionException {
        String sql = "SELECT * FROM booking WHERE booking_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBooking(rs);
                }
            }
        } catch (SQLException e) {
            throw new DbConnectionException("Error fetching booking by ID", e);
        }
        return null;
    }

    public void cancelBooking(int bookingId) throws DbConnectionException {
        String sql = "UPDATE booking SET is_deleted = true, status = ? WHERE booking_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, Booking.BookingStatus.CANCELLED.name());
            ps.setInt(2, bookingId);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                 throw new BookingFailedException("Could not cancel booking: Booking ID not found.");
            }
        } catch (SQLException e) {
            throw new DbConnectionException("Error cancelling booking", e);
        }
    }

    public List<Booking> getBookingsBySlotId(int slotId) throws DbConnectionException {
        List<Booking> slotBookings = new ArrayList<>();
        String sql = "SELECT * FROM booking WHERE slot_id = ? AND is_deleted = false AND status = 'CONFIRMED'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, slotId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    slotBookings.add(mapResultSetToBooking(rs));
                }
            }
        } catch (SQLException e) {
            throw new DbConnectionException("Error fetching bookings for slot " + slotId, e);
        }
        return slotBookings;
    }

    public List<Booking> getAllBookings() throws DbConnectionException {
        List<Booking> allBookings = new ArrayList<>();
        String sql = "SELECT * FROM booking";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                allBookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            throw new DbConnectionException("Error fetching all bookings", e);
        }
        return allBookings;
    }

    public int getNextBookingId() {
        return 0; 
    }

    public void addWaitlistedBooking(Booking booking) {
    }
}