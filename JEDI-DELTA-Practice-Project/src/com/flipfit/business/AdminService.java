package com.flipfit.business;

import com.flipfit.bean.FlipFitCustomer;
import com.flipfit.bean.FlipFitGymOwner;
import com.flipfit.exceptions.CentreNotFoundException;
import com.flipfit.exceptions.DbConnectionException;
import com.flipfit.exceptions.UserNotFoundException;
import com.flipfit.exceptions.WrongCredentialsException;

import java.util.Collection;

public interface AdminService {

    // Updated: Takes params and throws specific exceptions
    boolean login(String username, String password) throws WrongCredentialsException, DbConnectionException;
    
    void validateOwner(int ownerId) throws DbConnectionException, UserNotFoundException;
    
    void deleteOwner(int ownerId) throws DbConnectionException, UserNotFoundException;
    
    void viewFFCustomers() throws DbConnectionException;

    // REQUIRED by assignment
    void addGymCenter(int centerId, String gymName, String city, String state, int pincode, int capacity) throws DbConnectionException;
    
    void viewGymCenters() throws DbConnectionException;

    void addSlotInfo(int centerId, int slotId, String startTime, String endTime, int seats) throws DbConnectionException, CentreNotFoundException;
    
    void viewSlots(int centerId) throws DbConnectionException, CentreNotFoundException;

    // Utility
    FlipFitCustomer getCustomerById(int userId) throws DbConnectionException, UserNotFoundException;

    // Owner management
    void viewAllGymOwners() throws DbConnectionException;
    
    FlipFitGymOwner getOwnerById(int ownerId) throws DbConnectionException, UserNotFoundException;
    
    void approveOwner(int ownerId) throws DbConnectionException, UserNotFoundException;
}