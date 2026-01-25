package com.flipfit.business;

import com.flipfit.bean.Booking;
import com.flipfit.bean.Slot;
import com.flipfit.dao.BookingDAO;
import com.flipfit.dao.SlotDAO;

import java.util.List;

public class BookingServiceImpl implements BookingService {

    private final BookingDAO bookingDAO = BookingDAO.getInstance();
    private final SlotDAO slotDAO = SlotDAO.getInstance();

    @Override
    public Booking createBooking(int userId, int slotId) {
        Slot slot = slotDAO.getSlotById(slotId);
        if (slot != null && slot.getSeatsAvailable() > 0) {
            slot.setSeatsAvailable(slot.getSeatsAvailable() - 1);
            return bookingDAO.createBooking(userId, slotId);
        }
        return null;
    }

    @Override
    public List<Booking> getBookingsByUserId(int userId) {
        return bookingDAO.getBookingsByUserId(userId);
    }

    @Override
    public void cancelBooking(int bookingId) {
        Booking booking = bookingDAO.getBookingById(bookingId);
        if (booking != null) {
            bookingDAO.cancelBooking(bookingId);
            Slot slot = slotDAO.getSlotById(booking.getSlotId());
            if (slot != null) {
                slot.setSeatsAvailable(slot.getSeatsAvailable() + 1);
            }
        }
    }
}