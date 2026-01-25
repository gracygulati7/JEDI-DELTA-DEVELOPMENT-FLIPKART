package com.flipfit.repository;

import com.flipfit.bean.FlipFitGymCenter;
import com.flipfit.bean.Slot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlipFitBookingRepository {
    private static final SlotDAO slotDao = SlotDAO.getInstance();

    private static final Map<Integer, List<Slot>> userBookings = new HashMap<>();

    static {
        addInitialBooking(1, 101);
        addInitialBooking(2, 101);
    }

    private static void addInitialBooking(int userId, int slotId) {
        Slot slot = slotDao.getSlotById(slotId);
        if (slot != null && slot.getSeatsAvailable() > 0) {
            slot.setSeatsAvailable(slot.getSeatsAvailable() - 1);
            userBookings.computeIfAbsent(userId, k -> new ArrayList<>()).add(slot);
        }
    }

    public List<Slot> getBookedSlotsByUserId(int userId) {
        return userBookings.getOrDefault(userId, new ArrayList<>());
    }

}