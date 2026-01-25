package com.flipfit.bean;

public class Slot {
    private int slotId;
    private int centerId;
    private int startTime;
    private int seatsAvailable;
    private boolean booked;

    public Slot(int slotId, int centerId, int startTime, int seatsAvailable) {
        this.slotId = slotId;
        this.centerId = centerId;
        this.startTime = startTime;
        this.seatsAvailable = seatsAvailable;
        this.booked = false;
    }

    public int getSlotId() { return slotId; }
    public int getCenterId() { return centerId; }
    public int getStartTime() { return startTime; }
    public boolean isBooked() { return booked; }
    public void setBooked(boolean booked) { this.booked = booked; }

    @Override
    public String toString() {
        return "SlotId=" + slotId + ", StartTime=" + startTime + ", Booked=" + booked;
    }
}