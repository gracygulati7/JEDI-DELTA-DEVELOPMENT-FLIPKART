package com.flipfit.bean;

public class Slot {

    public int getSlotId() {
        return slotId;
    }

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public int getCenterId() {
        return centerId;
    }

    public void setCenterId(int centerId) {
        this.centerId = centerId;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getSeatsAvailable() {
        return seatsAvailable;
    }

    public void setSeatsAvailable(int seatsAvailable) {
        this.seatsAvailable = seatsAvailable;
    }

    private int slotId;
    private int centerId;
    private int startTime;
    private int seatsAvailable;

    public Slot(int slotId, int centerId, int startTime, int seatsAvailable) {
        this.slotId = slotId;
        this.centerId = centerId;
        this.startTime = startTime;
        this.seatsAvailable = seatsAvailable;
    }

    @Override
    public String toString() {
        return "SlotId=" + slotId +
               ", StartTime=" + startTime +
               ", SeatsAvailable=" + seatsAvailable;
    }
}
