package com.flipfit.bean;

import java.time.LocalDate;

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
    private LocalDate date;
    private int startTime;
    private int seatsAvailable;

    public Slot(int slotId, int centerId, LocalDate date, int startTime, int seatsAvailable) {
        this.slotId = slotId;
        this.centerId = centerId;
        this.date = date;
        this.startTime = startTime;
        this.seatsAvailable = seatsAvailable;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "SlotId=" + slotId +
               ", Date=" + (date != null ? date.toString() : "N/A") +
               ", StartTime=" + startTime +
               ", SeatsAvailable=" + seatsAvailable;
    }
}
