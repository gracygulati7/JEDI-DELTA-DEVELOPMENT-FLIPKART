package com.flipfit.business;

import com.flipfit.bean.Booking;
import com.flipfit.bean.Slot;
import com.flipfit.dao.BookingDAO;
import com.flipfit.dao.GymCentreDAO;
import com.flipfit.dao.SlotDAO;
import com.flipfit.dao.WaitlistDAO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * SlotScheduler - Handles all slot scheduling operations including:
 * - Waitlist management and promotion
 * - Finding nearest available slots
 * - Clearing expired slots
 * - Monitoring cancellations
 * - Preventing overbooking and time conflicts
 */
public class SlotScheduler {

    private final BookingDAO bookingDAO = BookingDAO.getInstance();
    private final SlotDAO slotDAO = SlotDAO.getInstance();
    private final WaitlistDAO waitlistDAO = WaitlistDAO.getInstance();
    private final NotificationService notificationService = NotificationServiceImpl.getInstance();
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Monitor and handle cancellations
     * When a booking is cancelled:
     * 1. Increase available seats
     * 2. Check if there are waitlisted customers
     * 3. Promote waitlisted customer if available
     */
    public void monitorCancellations(int bookingId) {
        System.out.println("\n[SCHEDULER] Processing cancellation for Booking ID: " + bookingId);
        
        Booking booking = bookingDAO.getBookingById(bookingId);
        if (booking == null || booking.isDeleted()) {
            System.out.println("[SCHEDULER] Booking not found or already cancelled");
            return;
        }

        int slotId = booking.getSlotId();
        int centerId = booking.getCenterId();
        int userId = booking.getUserId();

        // Mark booking as cancelled
        bookingDAO.cancelBooking(bookingId);
        System.out.println("[SCHEDULER] Booking " + bookingId + " marked as cancelled");

        // Free up the seat
        Slot slot = slotDAO.getSlotById(userId, slotId,centerId);
        if (slot != null) {
            slot.setSeatsAvailable(slot.getSeatsAvailable() + 1);
            System.out.println("[SCHEDULER] Seat freed. Available seats now: " + slot.getSeatsAvailable());

            // Trigger waitlist promotion if there are waitlisted customers
            if (waitlistDAO.hasWaitlistedCustomers(slotId)) {
                System.out.println("[SCHEDULER] Waitlist detected. Triggering promotion...");
                triggerWaitlistPromotion(slotId, centerId);
            }
        }
    }

    /**
     * Trigger waitlist promotion
     * Promotes the next waitlisted customer to a confirmed booking
     * Checks for time conflicts with their existing bookings
     */
    public void triggerWaitlistPromotion(int slotId, int centerId) {
        System.out.println("\n[SCHEDULER] Processing waitlist promotion for Slot ID: " + slotId);

        Integer nextUserId = waitlistDAO.removeFromWaitlist(slotId);
        if (nextUserId == null) {
            System.out.println("[SCHEDULER] No customers in waitlist");
            return;
        }

        Slot slot = slotDAO.getSlotById(nextUserId, slotId,centerId);
        if (slot == null || slot.isFull()) {
            System.out.println("[SCHEDULER] Slot is full, re-adding customer to waitlist");
            waitlistDAO.addToWaitlist(slotId, nextUserId);
            return;
        }

        // Check for time conflicts with user's other bookings
        if (hasTimeConflict(nextUserId, slot.getDate(), slot.getStartTime(), slot.getEndTime(), centerId)) {
            System.out.println("[SCHEDULER] ⚠️  Time conflict detected for User " + nextUserId);
            System.out.println("[SCHEDULER] Promotion cancelled - Customer has overlapping booking");
            waitlistDAO.addToWaitlist(slotId, nextUserId);
            return;
        }

        // Promote to confirmed booking
        slot.setSeatsAvailable(slot.getSeatsAvailable() - 1);
        Booking promotion = bookingDAO.createBooking(nextUserId, slotId);
        promotion.setStatus(Booking.BookingStatus.CONFIRMED);
        promotion.setCenterId(centerId);
        promotion.setSlotDate(slot.getDate());
        promotion.setStartTime(slot.getStartTime());
        promotion.setEndTime(slot.getEndTime());

        System.out.println("[SCHEDULER] ✓ Promoted User " + nextUserId + " from waitlist to Booking " + promotion.getBookingId());
        notificationService.sendWaitlistPromotion(nextUserId, slotId, centerId);
    }

    /**
     * Find nearest available slot for a user on the same date
     * Considers user's existing bookings to avoid conflicts
     * Returns slots that don't overlap with user's schedule
     */
    public Slot findNearestAvailableSlot(int userId, int centerId, LocalDate date, String preferredTime) {
        System.out.println("\n[SCHEDULER] Finding nearest available slot for User " + userId + 
                           " at Center " + centerId + " on " + date);

        List<Slot> centerSlots = slotDAO.getSlotsByCenterId(centerId);
        List<Slot> availableSlots = new ArrayList<>();

        LocalTime prefTime = parseTime(preferredTime);

        // Filter available slots on the same date
        for (Slot slot : centerSlots) {
            if (slot.getDate().equals(date) && !slot.isFull() && !slot.isExpired()) {
                // Check for time conflicts
                if (!hasTimeConflict(userId, slot.getDate(), slot.getStartTime(), slot.getEndTime(), centerId)) {
                    availableSlots.add(slot);
                }
            }
        }

        if (availableSlots.isEmpty()) {
            System.out.println("[SCHEDULER] No available slots found");
            return null;
        }

        // Sort by nearest to preferred time
        availableSlots.sort((s1, s2) -> {
            LocalTime t1 = parseTime(s1.getStartTime());
            LocalTime t2 = parseTime(s2.getStartTime());
            
            long diff1 = Math.abs(java.time.temporal.ChronoUnit.MINUTES.between(prefTime, t1));
            long diff2 = Math.abs(java.time.temporal.ChronoUnit.MINUTES.between(prefTime, t2));
            
            return Long.compare(diff1, diff2);
        });

        Slot nearest = availableSlots.get(0);
        System.out.println("[SCHEDULER] ✓ Found nearest available slot: " + nearest.getSlotId() + 
                           " at " + nearest.getStartTime() + " - " + nearest.getEndTime());
        return nearest;
    }

    /**
     * Clear expired slots
     * Removes or archives slots that have passed their date
     * Cleans up old booking data associated with expired slots
     */
    public void clearExpiredSlots() {
        System.out.println("\n[SCHEDULER] Clearing expired slots...");

        List<Slot> allSlots = slotDAO.getAllSlots();
        int expiredCount = 0;
        LocalDate today = LocalDate.now();

        for (Slot slot : allSlots) {
            if (slot.getDate().isBefore(today)) {
                expiredCount++;
                System.out.println("[SCHEDULER] Expired: Slot " + slot.getSlotId() + 
                                   " (Date: " + slot.getDate() + ")");
            }
        }

        System.out.println("[SCHEDULER] ✓ Found " + expiredCount + " expired slots");
        System.out.println("[SCHEDULER] Note: Expired slots are marked but retained for audit purposes");
    }

    /**
     * Check for time conflicts
     * Validates that a new booking doesn't overlap with user's existing bookings
     * Can also prevent double-booking at the same time across different centers
     */
    private boolean hasTimeConflict(int userId, LocalDate date, String startTime, String endTime, int newCenterId) {
        List<Booking> userBookings = bookingDAO.getBookingsByUserId(userId);
        LocalTime newStart = parseTime(startTime);
        LocalTime newEnd = parseTime(endTime);

        for (Booking booking : userBookings) {
            if (booking.isDeleted() || booking.getStatus() == Booking.BookingStatus.CANCELLED) {
                continue;
            }

            // Check if on the same date
            if (booking.getSlotDate() != null && booking.getSlotDate().equals(date)) {
                LocalTime bookingStart = parseTime(booking.getStartTime());
                LocalTime bookingEnd = parseTime(booking.getEndTime());

                // Check for overlap
                if (timesOverlap(newStart, newEnd, bookingStart, bookingEnd)) {
                    System.out.println("[SCHEDULER] Conflict: User has booking at " + 
                                       booking.getStartTime() + " - " + booking.getEndTime());
                    return true;
                }

                // Check if same time at same center (prevent same-gym re-booking)
                if (booking.getCenterId() == newCenterId && 
                    newStart.equals(bookingStart) && newEnd.equals(bookingEnd)) {
                    System.out.println("[SCHEDULER] Conflict: User already booked this exact slot");
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Check if time periods overlap
     */
    private boolean timesOverlap(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    /**
     * Parse time string in HH:mm format
     */
    private LocalTime parseTime(String timeStr) {
        try {
            return LocalTime.parse(timeStr, TIME_FORMATTER);
        } catch (Exception e) {
            System.out.println("[SCHEDULER] Error parsing time: " + timeStr);
            return LocalTime.MIDNIGHT;
        }
    }

    /**
     * Validate booking to prevent overbooking
     */
    public boolean validateBooking(int userId, int slotId, int centerId) {
        System.out.println("\n[SCHEDULER] Validating booking for User " + userId + 
                           " at Slot " + slotId + " in Center " + centerId);

        Slot slot = slotDAO.getSlotById(userId, slotId,centerId);
        if (slot == null) {
            System.out.println("[SCHEDULER] ✗ Slot not found");
            return false;
        }

        if (slot.isExpired()) {
            System.out.println("[SCHEDULER] ✗ Slot has expired");
            return false;
        }

        if (slot.isFull()) {
            System.out.println("[SCHEDULER] ✗ Slot is full. Adding to waitlist...");
            waitlistDAO.addToWaitlist(slotId, userId);
            Booking waitlistedBooking = bookingDAO.createWaitlistingBooking(userId, slotId);
            waitlistedBooking.setStatus(Booking.BookingStatus.WAITLISTED);
            waitlistedBooking.setCenterId(centerId);
            waitlistedBooking.setSlotDate(slot.getDate());
            waitlistedBooking.setStartTime(slot.getStartTime());
            waitlistedBooking.setEndTime(slot.getEndTime());
            // bookingDAO.addWaitlistedBooking(waitlistedBooking);
            return false; // Not immediately available
        }

        if (hasTimeConflict(userId, slot.getDate(), slot.getStartTime(), slot.getEndTime(), centerId)) {
            System.out.println("[SCHEDULER] ✗ Time conflict detected");
            return false;
        }

        System.out.println("[SCHEDULER] ✓ Booking validated successfully");
        return true;
    }

    /**
     * Get waitlist information for a slot
     */
    public int getWaitlistSize(int slotId) {
        return waitlistDAO.getWaitlistSize(slotId);
    }

    /**
     * Get all user bookings for a specific date
     */
    public List<Booking> getUserBookingsForDate(int userId, LocalDate date) {
        List<Booking> allBookings = bookingDAO.getBookingsByUserId(userId);
        List<Booking> dateBookings = new ArrayList<>();

        for (Booking booking : allBookings) {
            if (booking.getSlotDate() != null && booking.getSlotDate().equals(date) && 
                !booking.isDeleted() && booking.getStatus() != Booking.BookingStatus.CANCELLED) {
                dateBookings.add(booking);
            }
        }

        return dateBookings;
    }
}