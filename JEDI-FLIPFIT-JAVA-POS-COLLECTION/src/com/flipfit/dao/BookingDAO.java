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
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        bookings.add(booking);
        return booking;
    }

    public void addWaitlistedBooking(Booking booking) {
        bookings.add(booking);
    }
    
    public Booking createWaitlistingBooking(int userId, int slotId) {
        Booking booking = new Booking();
        booking.setBookingId(nextBookingId++); // increment once
        booking.setUserId(userId);
        booking.setSlotId(slotId);
        booking.setStatus(Booking.BookingStatus.WAITLISTED);
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
                booking.setStatus(Booking.BookingStatus.CANCELLED);
                break;
            }
        }
    }

    public List<Booking> getBookingsBySlotId(int slotId) {
        List<Booking> slotBookings = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getSlotId() == slotId && !booking.isDeleted() && 
                booking.getStatus() == Booking.BookingStatus.CONFIRMED) {
                slotBookings.add(booking);
            }
        }
        return slotBookings;
    }

    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }

    public int getNextBookingId() {
        return nextBookingId;
    }
}