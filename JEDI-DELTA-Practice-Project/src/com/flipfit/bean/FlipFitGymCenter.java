package com.flipfit.bean;

import java.util.ArrayList;
import java.util.List;

public class FlipFitGymCenter {

    private int centerId;
    private String gymName;
    private int ownerId;
    private int capacity;
    private boolean approved;
    private String city;
    private String state;
    private int pincode;

    private List<Slot> slots = new ArrayList<>();

    public FlipFitGymCenter(int centerId, String gymName, String city, String state, int pincode, int capacity) {
        this.centerId = centerId;
        this.gymName = gymName;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.capacity = capacity;
        this.approved = true;
    }

    public int getGymId() { return centerId; }
    public String getGymName() { return gymName; }
    public String getLocation() { return city + ", " + state; }
    public int getCenterId() { return centerId; }
    public int getOwnerId() { return ownerId; }
    public int getCapacity() { return capacity; }
    public boolean isApproved() { return approved; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public int getPincode() { return pincode; }
    public List<Slot> getSlots() { return slots; }
    public void addSlot(Slot slot) { slots.add(slot); }
    public void setApproved(boolean approved) { this.approved = approved; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    @Override
    public String toString() {
        return "CenterId=" + centerId +
               ", GymName=" + gymName +
               ", City=" + city +
               ", State=" + state +
               ", Pincode=" + pincode +
               ", Capacity=" + capacity;
    }
}