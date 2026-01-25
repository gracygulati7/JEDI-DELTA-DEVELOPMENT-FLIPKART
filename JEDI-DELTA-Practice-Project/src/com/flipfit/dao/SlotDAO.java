package com.flipfit.dao;

import com.flipfit.bean.Slot;
import java.util.ArrayList;
import java.util.List;

public class SlotDAO {

    private static SlotDAO instance = null;
    private final List<Slot> slots = new ArrayList<>();

    private SlotDAO() {}

    public static SlotDAO getInstance() {
        if (instance == null) {
            instance = new SlotDAO();
        }
        return instance;
    }

    public void addSlot(Slot slot) {
        slots.add(slot);
    }

    public List<Slot> getSlotsByCenterId(int centerId) {
        List<Slot> centerSlots = new ArrayList<>();
        for (Slot slot : slots) {
            if (slot.getCenterId() == centerId) {
                centerSlots.add(slot);
            }
        }
        return centerSlots;
    }

    public Slot getSlotById(int slotId) {
        for (Slot slot : slots) {
            if (slot.getSlotId() == slotId) {
                return slot;
            }
        }
        return null;
    }
}
