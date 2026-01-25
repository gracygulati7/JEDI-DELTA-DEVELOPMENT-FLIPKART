package com.flipfit.repository;

import com.flipfit.bean.FlipFitGymCenter;
import com.flipfit.bean.Slot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlipFitGymCenterRepository {

    private static Map<Integer, List<FlipFitGymCenter>> centersByPincode = new HashMap<>();
    private static Map<Integer, List<FlipFitGymCenter>> centersByOwner = new HashMap<>();
    private static Map<Integer, FlipFitGymCenter> centersById = new HashMap<>();

    static {
        // Merged logic into one block using one consistent method
        FlipFitGymCenterRepository repo = new FlipFitGymCenterRepository();

        FlipFitGymCenter c1 = new FlipFitGymCenter(1, 10, "Devarabisanhalli", "Karnataka", 560001, 50);
        c1.addSlot(new Slot(101, 1, 7, 20));
        repo.addCenter(c1);

        repo.addCenter(new FlipFitGymCenter(2, 10, "Bellandur", "Karnataka", 560001, 40));
        repo.addCenter(new FlipFitGymCenter(3, 11, "Marathalli", "Karnataka", 560037, 60));
        repo.addCenter(new FlipFitGymCenter(4, 12, "Whitefield", "Maharashtra", 400001, 45));
    }

    public void addCenter(FlipFitGymCenter center) {
        centersById.put(center.getCenterId(), center);

        centersByPincode
                .computeIfAbsent(center.getPincode(), k -> new ArrayList<>())
                .add(center);

        centersByOwner
                .computeIfAbsent(center.getOwnerId(), k -> new ArrayList<>())
                .add(center);
    }

    public FlipFitGymCenter getCenterById(int centerId) {
        return centersById.get(centerId);
    }

    public List<FlipFitGymCenter> getCentersByOwnerId(int ownerId) {
        return centersByOwner.getOrDefault(ownerId, new ArrayList<>());
    }

    public List<FlipFitGymCenter> getCentersByPincode(int pincode) {
        return centersByPincode.getOrDefault(pincode, new ArrayList<>());
    }
}