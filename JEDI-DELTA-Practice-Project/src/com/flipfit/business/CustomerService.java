package com.flipfit.business;

import com.flipfit.exceptions.DbConnectionException;
import com.flipfit.exceptions.UserNotFoundException;
import java.util.List;

public interface CustomerService {
    void viewBookedSlots(int userId);
    
    boolean checkBookingConflicts(int userId, int slotId);
    
    List<Object> viewCentres(String city);
    
    // Updated: Now throws exceptions because it calls DAO
    boolean makePayment(int userId, int amount) throws DbConnectionException, UserNotFoundException;
    
    // Updated: Now throws exceptions because it calls DAO
    void viewPaymentInfo(int userId) throws DbConnectionException, UserNotFoundException;
}