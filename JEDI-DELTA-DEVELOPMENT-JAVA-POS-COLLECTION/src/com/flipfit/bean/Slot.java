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

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(this.date);
    }

    private int slotId;
    private int centerId;
    private LocalDate date;
    private String startTime;
    private String endTime;
    private int seatsAvailable;
    private int totalSeats;
    private int fee;

    public Slot(int slotId, int centerId, LocalDate date, String startTime, String endTime, int seatsAvailable) {
        this.slotId = slotId;
        this.centerId = centerId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.seatsAvailable = seatsAvailable;
        this.totalSeats = seatsAvailable;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getBookedSeats() {
        return totalSeats - seatsAvailable;
    }

    public boolean isFull() {
        return seatsAvailable <= 0;
    }
    
    public int getFee() {
    	return fee;
    }
    
    public void setFee(int fee) {
    	this.fee=fee;
    }

    @Override
    public String toString() {
        return "SlotId=" + slotId +
               ", Date=" + (date != null ? date.toString() : "N/A") +
               ", Time: " + startTime + " - " + endTime +
               ", SeatsAvailable=" + seatsAvailable + "/" + totalSeats;
    }
}