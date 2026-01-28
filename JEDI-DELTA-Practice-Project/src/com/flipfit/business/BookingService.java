package com.flipfit.business;

import com.flipfit.bean.Booking;
import com.flipfit.exceptions.BookingFailedException;
import com.flipfit.exceptions.DbConnectionException;

import java.util.List;

public interface BookingService {
    // Throws specific exceptions so the Controller knows what went wrong
    Booking createBooking(int userId, int slotId, int centerId) throws BookingFailedException, DbConnectionException;
    
    List<Booking> getBookingsByUserId(int userId) throws DbConnectionException;
    
    void cancelBooking(int bookingId) throws BookingFailedException, DbConnectionException;
}