package com.flipfit.business;

import com.flipfit.bean.Booking;
import com.flipfit.bean.Slot;
import com.flipfit.dao.BookingDAO;
import com.flipfit.dao.SlotDAO;

import java.time.LocalDate;
import java.util.List;

public class BookingServiceImpl implements BookingService {
    private final BookingDAO bookingDAO = BookingDAO.getInstance();
    private final SlotDAO slotDAO = SlotDAO.getInstance();
    private final SlotScheduler slotScheduler = new SlotScheduler();
    private final NotificationService notificationService = NotificationServiceImpl.getInstance();

    @Override
    public Booking createBooking(int userId, int slotId,int centerId) {
        System.out.println("\n[BOOKING] Creating booking for User " + userId + " at Slot " + slotId);
        
        
        Slot slot=slotDAO.getSlotById(userId, slotId,centerId);
        if (slot == null) {
            System.out.println("[BOOKING] ✗ Slot not found");
            return null;
        }

        // Get center details
        
        LocalDate slotDate = slot.getDate();
        String startTime = slot.getStartTime();
        String endTime = slot.getEndTime();

        // Validate booking (no overbooking, no time conflicts)
        if (!slotScheduler.validateBooking(userId, slotId, centerId)) {
            return null;
        }

        // Create confirmed booking
        slot.setSeatsAvailable(slot.getSeatsAvailable() - 1);
        Booking booking = bookingDAO.createBooking(userId, slotId);
        
        // Set additional booking details
        booking.setCenterId(centerId);
        booking.setSlotDate(slotDate);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);

        System.out.println("[BOOKING] ✓ Booking " + booking.getBookingId() + " confirmed");
        notificationService.sendBookingConfirmation(userId, slotId, centerId);
        
        return booking;
    }

    @Override
    public List<Booking> getBookingsByUserId(int userId) {
        return bookingDAO.getBookingsByUserId(userId);
    }

    @Override
    public void cancelBooking(int bookingId) {
        System.out.println("\n[BOOKING] Cancelling booking ID: " + bookingId);
        
        // Use scheduler to handle cancellation, waitlist promotion, etc.
        slotScheduler.monitorCancellations(bookingId);
        
        System.out.println("[BOOKING] ✓ Booking cancelled and processed");
    }

    /**
     * Get available slots for a user on a specific date and center
     */
    public List<Slot> getAvailableSlotsForCenter(int centerId, LocalDate date) {
        return slotDAO.getAvailableSlotsByDateAndCenter(centerId, date);
    }

    /**
     * Find nearest available slot
     */
    public Slot findNearestSlot(int userId, int centerId, LocalDate date, String preferredTime) {
        return slotScheduler.findNearestAvailableSlot(userId, centerId, date, preferredTime);
    }

    /**
     * Get user's bookings for a specific date
     */
    public List<Booking> getUserBookingsForDate(int userId, LocalDate date) {
        return slotScheduler.getUserBookingsForDate(userId, date);
    }

    /**
     * Get waitlist size for a slot
     */
    public int getWaitlistSize(int slotId) {
        return slotScheduler.getWaitlistSize(slotId);
    }

    /**
     * Clear expired slots (typically called by scheduled task)
     */
    public void clearExpiredSlots() {
        slotScheduler.clearExpiredSlots();
    }
}