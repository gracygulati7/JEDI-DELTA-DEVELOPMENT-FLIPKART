package com.flipfit.business;

public class CustomerServiceImpl implements CustomerService{

	@Override
	public void viewCenters() {
		// TODO Auto-generated method stub
		//fetch from DAO
		
	}

	@Override
	public void viewBookedSlots(int userId) {
		// TODO Auto-generated method stub
		//fetch bookings
		
	}

	@Override
	public boolean makeBooking(int userId, int slotId) {
		// TODO Auto-generated method stub
		//check availability
		//reduce seats
		//create booking
		return true;
	}

	@Override
	public void cancelBooking(int bookingId) {
		// TODO Auto-generated method stub
		//restore seat
		//notify waitlist
		
	}

}
