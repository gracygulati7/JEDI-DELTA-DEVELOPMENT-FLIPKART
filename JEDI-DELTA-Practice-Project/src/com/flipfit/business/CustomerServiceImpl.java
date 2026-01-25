package com.flipfit.business;

public class CustomerServiceImpl implements CustomerService {
    @Override
    public void viewBookedSlots(int userId) {
        System.out.println("Displaying booked slots for User: " + userId);
    }

    @Override
    public boolean checkBookingConflicts(int userId, int slotId) {
        return false; 
    }

    @Override
    public boolean makePayment(int userId, int amount) {
        System.out.println("Payment of " + amount + " successful for user " + userId);
        return true;
    }

    @Override
    public void editDetails(int userId) {
        System.out.println("Updating profile for " + userId);
    }

    @Override
    public java.util.List<Object> viewCentres(String city) {
        return null; 
    }
}