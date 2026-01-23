package com.flipfit.business;

public interface CustomerService {
	void viewCenters();
	void viewBookedSlots(int userId);
	boolean makeBooking(int userId, int slotId);
	void cancelBooking(int bookingId);
}
