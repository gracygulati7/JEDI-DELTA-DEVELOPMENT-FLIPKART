package com.flipfit.business;

import com.flipfit.bean.Booking;
import com.flipfit.bean.Slot;
import com.flipfit.dao.BookingDAO;
import com.flipfit.dao.SlotDAO;
import com.flipfit.exceptions.BookingFailedException;
import com.flipfit.exceptions.DbConnectionException;

import java.util.List;
import java.util.stream.Collectors;

public class BookingServiceImpl implements BookingService {
    
    private final BookingDAO bookingDAO = BookingDAO.getInstance();
    private final SlotDAO slotDAO = SlotDAO.getInstance();
    private final SlotScheduler slotScheduler = new SlotScheduler();
    private final NotificationService notificationService = NotificationServiceImpl.getInstance();

    @Override
    public Booking createBooking(int userId, int slotId, int centerId) throws BookingFailedException, DbConnectionException {
        
        // 1. Get Slot (might throw DbConnectionException)
        Slot slot = slotDAO.getSlotById(slotId);
        
        // 2. Validate Slot existence
        if (slot == null) {
            throw new BookingFailedException("Slot with ID " + slotId + " not found.");
        }

        // 3. Validate Logic (Scheduler)
        // Note: Assuming validateBooking returns boolean. 
        if (!slotScheduler.validateBooking(userId, slotId, centerId)) {
            throw new BookingFailedException("Booking validation failed. The slot may be full or already booked.");
        }

        // 4. Update memory/bean (optional, as DB triggers usually handle seats, but good for local consistency)
        slot.setSeatsAvailable(slot.getSeatsAvailable() - 1);

        // 5. Persist to DB
        Booking booking = bookingDAO.createBooking(userId, slotId);
        if (booking == null) {
            throw new BookingFailedException("Failed to persist booking.");
        }

        // 6. Set extra details for the return object
        booking.setCenterId(centerId);
        booking.setSlotDate(slot.getDate());
        booking.setStartTime(slot.getStartTime());
        booking.setEndTime(slot.getEndTime());
        booking.setStatus(Booking.BookingStatus.CONFIRMED);

        // 7. Send Notification
        notificationService.sendBookingConfirmation(userId, slotId, centerId);

        return booking;
    }

    @Override
    public List<Booking> getBookingsByUserId(int userId) throws DbConnectionException {
        // stream() handles the logic, but the DAO call itself propagates DbConnectionException
        return bookingDAO.getBookingsByUserId(userId).stream()
                .filter(b -> !b.isDeleted() && b.getStatus() == Booking.BookingStatus.CONFIRMED)
                .collect(Collectors.toList());
    }

    @Override
    public void cancelBooking(int bookingId) throws BookingFailedException, DbConnectionException {
        // Direct call to DAO ensures the DB updates and exceptions are handled
        bookingDAO.cancelBooking(bookingId);
        
        // Optional: Keep your scheduler logic if it handles Waitlist promotion
        slotScheduler.monitorCancellations(bookingId);
    }
}