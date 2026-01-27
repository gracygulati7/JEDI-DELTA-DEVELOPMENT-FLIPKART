package com.flipfit.dao;

import com.flipfit.bean.FlipFitGymOwner;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

public class OwnerDAO {
    private static OwnerDAO instance = null;
    private final Map<Integer, FlipFitGymOwner> owners = new HashMap<>();
    private int nextOwnerId = 301;

    private OwnerDAO() {}

    public static OwnerDAO getInstance() {
        if (instance == null) instance = new OwnerDAO();
        return instance;
    }

    public FlipFitGymOwner addOwner(String fullName) {
        FlipFitGymOwner o = new FlipFitGymOwner(nextOwnerId++, fullName, null, null, null);
        owners.put(o.getOwnerId(), o);
        return o;
    }

    public FlipFitGymOwner getOwnerByName(String name) {
        for (FlipFitGymOwner o : owners.values()) {
            if (o.getName() != null && o.getName().equalsIgnoreCase(name)) return o;
        }
        return null;
    }

    public FlipFitGymOwner getOrCreateOwnerByName(String name) {
        FlipFitGymOwner o = getOwnerByName(name);
        if (o != null) return o;
        return addOwner(name);
    }

    public FlipFitGymOwner getOwnerById(int id) { return owners.get(id); }
    public Collection<FlipFitGymOwner> getAllOwners() { return owners.values(); }
}
