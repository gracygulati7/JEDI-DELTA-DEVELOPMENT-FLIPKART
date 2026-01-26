package com.flipfit.dao;

import com.flipfit.bean.Waitlist;
import java.util.ArrayList;
import java.util.List;

public class WaitlistDAO {

    private static WaitlistDAO instance = null;
    private final List<Waitlist> waitlists = new ArrayList<>();
    private int nextWaitlistId = 2001;

    private WaitlistDAO() {}

    public static WaitlistDAO getInstance() {
        if (instance == null) {
            instance = new WaitlistDAO();
        }
        return instance;
    }

    /**
     * Create or get waitlist for a slot
     */
    public Waitlist getOrCreateWaitlist(int slotId) {
        for (Waitlist waitlist : waitlists) {
            if (waitlist.getSlotId() == slotId) {
                return waitlist;
            }
        }
        // Create new waitlist if doesn't exist
        Waitlist newWaitlist = new Waitlist(nextWaitlistId++, slotId);
        waitlists.add(newWaitlist);
        return newWaitlist;
    }

    /**
     * Get waitlist for a specific slot
     */
    public Waitlist getWaitlistBySlotId(int slotId) {
        for (Waitlist waitlist : waitlists) {
            if (waitlist.getSlotId() == slotId && !waitlist.isEmpty()) {
                return waitlist;
            }
        }
        return null;
    }

    /**
     * Get all waitlists
     */
    public List<Waitlist> getAllWaitlists() {
        return new ArrayList<>(waitlists);
    }

    /**
     * Add customer to waitlist
     */
    public void addToWaitlist(int slotId, int userId) {
        Waitlist waitlist = getOrCreateWaitlist(slotId);
        waitlist.addCustomer(userId);
    }

    /**
     * Remove customer from waitlist (poll next customer)
     */
    public Integer removeFromWaitlist(int slotId) {
        Waitlist waitlist = getWaitlistBySlotId(slotId);
        if (waitlist != null) {
            return waitlist.getNextInLine();
        }
        return null;
    }

    /**
     * Check if slot has waitlisted customers
     */
    public boolean hasWaitlistedCustomers(int slotId) {
        Waitlist waitlist = getWaitlistBySlotId(slotId);
        return waitlist != null && !waitlist.isEmpty();
    }

    /**
     * Get waitlist size for a slot
     */
    public int getWaitlistSize(int slotId) {
        Waitlist waitlist = getWaitlistBySlotId(slotId);
        return waitlist != null ? waitlist.getCustomerQueue().size() : 0;
    }
}