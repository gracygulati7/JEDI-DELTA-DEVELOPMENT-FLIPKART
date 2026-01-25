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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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
    private String startTime;
    private String endTime;
    private int seatsAvailable;

    public Slot(int slotId, int centerId, LocalDate date, String startTime, String endTime, int seatsAvailable) {
        this.slotId = slotId;
        this.centerId = centerId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
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
               ", Time: " + startTime + " - " + endTime +
               ", SeatsAvailable=" + seatsAvailable;
    }
}