package com.flipfit.repository;

import com.flipfit.bean.Slot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlipFitBookingRepository {

    // Storage: Maps UserId -> List of their booked slots
    private static final Map<Integer, List<Slot>> userBookings = new HashMap<>();

    static {
        // Dummy Data for User 1
        List<Slot> user1Slots = new ArrayList<>();
        user1Slots.add(new Slot(101, 10, 7, 20)); // Slot 101, Center 10, 7 AM
        user1Slots.add(new Slot(105, 11, 18, 15)); // Slot 105, Center 11, 6 PM
        userBookings.put(1, user1Slots);

        // Dummy Data for User 2
        List<Slot> user2Slots = new ArrayList<>();
        user2Slots.add(new Slot(202, 10, 9, 10)); // Slot 202, Center 10, 9 AM
        userBookings.put(2, user2Slots);
    }

    /**
     * Retrieves the list of slots for a specific user.
     * Returns an empty list if no bookings exist.
     */
    public List<Slot> getBookedSlotsByUserId(int userId) {
        return userBookings.getOrDefault(userId, new ArrayList<>());
    }
}