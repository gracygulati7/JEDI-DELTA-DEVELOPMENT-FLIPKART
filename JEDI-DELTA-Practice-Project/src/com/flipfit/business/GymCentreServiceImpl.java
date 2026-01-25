package com.flipfit.business;

import com.flipfit.bean.FlipFitGymCenter;
import com.flipfit.bean.Slot;
import com.flipfit.repository.FlipFitGymCenterRepository;
import java.util.ArrayList;
import java.util.List;

public class GymCentreServiceImpl implements GymCentreService {

    private FlipFitGymCenterRepository centerRepo = new FlipFitGymCenterRepository();

    public List<Slot> getAvailableSlotsByCenter(int centerId) {
        List<Slot> availableSlots = new ArrayList<>();
        FlipFitGymCenter center = centerRepo.getCenterById(centerId);

        if (center != null) {
            for (Slot slot : center.getSlots()) {
                if (slot.getSeatsAvailable() > 0) {
                    availableSlots.add(slot);
                }
            }
        }
        return availableSlots;
    }


    public List<Slot> getAllSlotsByCenter(int centerId) {
        FlipFitGymCenter center = centerRepo.getCenterById(centerId);
        if (center != null) {
            return center.getSlots(); // Returns only the slots belonging to THIS center
        }
        return new ArrayList<>();
    }

    public int getSeatsLeftInSlot(int centerId, int slotId) {
        FlipFitGymCenter center = centerRepo.getCenterById(centerId);
        if (center != null) {
            for (Slot slot : center.getSlots()) {
                if (slot.getSlotId() == slotId) {
                    return slot.getSeatsAvailable();
                }
            }
        }
        return 0; // Slot not found or full
    }
}