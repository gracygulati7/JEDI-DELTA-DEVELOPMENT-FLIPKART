package com.flipfit.dao;

import com.flipfit.bean.Booking;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    private static BookingDAO instance = null;
    private final List<Booking> bookings = new ArrayList<>();
    private int nextBookingId = 1;

    private BookingDAO() {}

    public static BookingDAO getInstance() {
        if (instance == null) {
            instance = new BookingDAO();
        }
        return instance;
    }

    public Booking createBooking(int userId, int slotId) {
        Booking booking = new Booking();
        booking.setBookingId(nextBookingId++);
        booking.setUserId(userId);
        booking.setSlotId(slotId);
        booking.setDeleted(false);
        bookings.add(booking);
        return booking;
    }

    public List<Booking> getBookingsByUserId(int userId) {
        List<Booking> userBookings = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getUserId() == userId && !booking.isDeleted()) {
                userBookings.add(booking);
            }
        }
        return userBookings;
    }

    public Booking getBookingById(int bookingId) {
        for (Booking booking : bookings) {
            if (booking.getBookingId() == bookingId) {
                return booking;
            }
        }
        return null;
    }

    public void cancelBooking(int bookingId) {
        for (Booking booking : bookings) {
            if (booking.getBookingId() == bookingId) {
                booking.setDeleted(true);
                break;
            }
        }
    }

    public List<Booking> getBookingsBySlotId(int slotId) {
        List<Booking> slotBookings = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getSlotId() == slotId && !booking.isDeleted()) {
                slotBookings.add(booking);
            }
        }
        return slotBookings;
    }
}
