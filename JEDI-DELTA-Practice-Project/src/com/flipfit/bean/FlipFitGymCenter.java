package com.flipfit.bean;

import java.util.ArrayList;
import java.util.List;

public class FlipFitGymCenter {

    private int centerId;
    private int ownerId;
    private int capacity;
    private boolean approved;
    private String city;
    private String state;
    private int pincode;
    private List<Slot> slots = new ArrayList<>();

    public FlipFitGymCenter(int centerId, int ownerId, String city, String state, int pincode, int capacity) {
        this.centerId = centerId;
        this.ownerId = ownerId;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.capacity = capacity;
        this.approved = true;
    }

    public int getCenterId() { return centerId; }
    public int getOwnerId() { return ownerId; }
    public int getPincode() { return pincode; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public int getCapacity() { return capacity; }
    public List<Slot> getSlots() { return slots; }

    public void addSlot(Slot slot) { this.slots.add(slot); }

    @Override
    public String toString() {
        return "CenterId=" + centerId +
               ", City=" + city +
               ", State=" + state +
               ", Pincode=" + pincode +
               ", Capacity=" + capacity;
    }
}