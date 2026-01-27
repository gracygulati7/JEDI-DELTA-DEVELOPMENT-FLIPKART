package com.flipfit.bean;

import java.time.LocalDateTime;
import java.time.LocalDate;

public class Booking {

    private int bookingId;
    private int userId;
    private int slotId;
    private int centerId;
    private LocalDate slotDate;
    private String startTime;
    private String endTime;
    private boolean isDeleted;
    private LocalDateTime bookingDate;
    private BookingStatus status; // CONFIRMED, WAITLISTED, CANCELLED

    public enum BookingStatus {
        CONFIRMED, WAITLISTED, CANCELLED
    }

    public Booking() {
        this.bookingDate = LocalDateTime.now();
        this.isDeleted = false;
        this.status = BookingStatus.CONFIRMED;
    }

    public Booking(int bookingId, int userId, int slotId) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.slotId = slotId;
        this.isDeleted = false;
        this.bookingDate = LocalDateTime.now();
        this.status = BookingStatus.CONFIRMED;
    }

    public Booking(int bookingId, int userId, int slotId, int centerId, LocalDate slotDate, String startTime, String endTime) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.slotId = slotId;
        this.centerId = centerId;
        this.slotDate = slotDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isDeleted = false;
        this.bookingDate = LocalDateTime.now();
        this.status = BookingStatus.CONFIRMED;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

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

    public LocalDate getSlotDate() {
        return slotDate;
    }

    public void setSlotDate(LocalDate slotDate) {
        this.slotDate = slotDate;
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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Booking [BookingId=" + bookingId + ", UserId=" + userId + ", SlotId=" + slotId
                + ", CenterId=" + centerId + ", SlotDate=" + slotDate + ", Time=" + startTime + "-" + endTime
                + ", Status=" + status + ", BookingDate=" + bookingDate + ", IsDeleted=" + isDeleted + "]";
    }
}