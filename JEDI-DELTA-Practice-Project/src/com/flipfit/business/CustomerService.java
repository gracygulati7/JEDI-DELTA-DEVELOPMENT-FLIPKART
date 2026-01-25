package com.flipfit.business;
import java.util.List;

public interface CustomerService {
    void viewBookedSlots(int userId);
    boolean checkBookingConflicts(int userId, int slotId);
    List<Object> viewCentres(String city);
    boolean makePayment(int userId, int amount);
    void editDetails(int userId);
}