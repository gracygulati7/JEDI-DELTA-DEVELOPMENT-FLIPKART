package com.flipfit.bean;

import java.time.LocalDateTime;

public class Booking {

	private int bookingId;
	private int userId;
	private int slotId;
	private boolean isDeleted;
	private LocalDateTime bookingDate;

	public Booking() {
		this.bookingDate = LocalDateTime.now();
		this.isDeleted = false;
	}

	public Booking(int bookingId, int userId, int slotId) {
		this.bookingId = bookingId;
		this.userId = userId;
		this.slotId = slotId;
		this.isDeleted = false;
		this.bookingDate = LocalDateTime.now();
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

	@Override
	public String toString() {
		return "Booking [BookingId=" + bookingId + ", UserId=" + userId + ", SlotId=" + slotId
				+ ", BookingDate=" + bookingDate + ", IsDeleted=" + isDeleted + "]";
	}
}
