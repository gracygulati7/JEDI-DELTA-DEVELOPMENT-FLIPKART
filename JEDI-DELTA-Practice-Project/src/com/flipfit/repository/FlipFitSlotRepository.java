package com.flipfit.repository;

import com.flipfit.bean.Slot;
import com.flipfit.dao.SlotDAO;
import java.time.LocalDate;
import java.util.List;

public class FlipFitSlotRepository {

    // Use the Singleton SlotDAO as the actual data storage
    private static final SlotDAO slotDao = SlotDAO.getInstance();

    static {
        // Hardcoding data using the new Slot constructor (includes LocalDate)
        // Constructor: Slot(slotId, centerId, date, startTime, seatsAvailable)

        LocalDate today = LocalDate.now();

        slotDao.addSlot(new Slot(101, 1, today, 7, 20));
        slotDao.addSlot(new Slot(102, 1, today, 8, 20));
        slotDao.addSlot(new Slot(201, 2, today, 18, 15));
        slotDao.addSlot(new Slot(301, 3, today, 9, 60));
    }

    /**
     * Fetches a slot by ID from the DAO
     */
    public Slot getSlotById(int slotId) {
        return slotDao.getSlotById(slotId);
    }

    /**
     * Fetches all slots currently in the system
     */
    public List<Slot> getAllSlots() {
        return slotDao.getAllSlots();
    }

    /**
     * Fetches slots for a specific center from the DAO
     */
    public List<Slot> getSlotsByCenterId(int centerId) {
        return slotDao.getSlotsByCenterId(centerId);
    }
}