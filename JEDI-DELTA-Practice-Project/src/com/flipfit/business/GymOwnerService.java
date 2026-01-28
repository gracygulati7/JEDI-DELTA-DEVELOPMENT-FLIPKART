package com.flipfit.business;

import com.flipfit.bean.FlipFitGymCenter;
import com.flipfit.bean.FlipFitGymOwner;
import com.flipfit.exceptions.DbConnectionException;
import com.flipfit.exceptions.UserNotFoundException;

import java.util.List;

public interface GymOwnerService {
    // Registration
    FlipFitGymOwner registerOwner(String name, String pan, String aadhaar, String gstin) throws DbConnectionException, UserNotFoundException;
    
    void addCentre(int ownerId, int centerId, String gymName, String city, String state, int pincode, int capacity) throws DbConnectionException;
    
    List<FlipFitGymCenter> viewCentres(int ownerId) throws DbConnectionException;
    
    void addSlot(int centerId, int slotId, java.time.LocalDate date, String startTime, String endTime, int seats) throws DbConnectionException;
    
    void viewSlots(int centerId) throws DbConnectionException;
    
    void viewCustomers(int centreId) throws DbConnectionException, UserNotFoundException;
    
    void viewPayments(int ownerId);
    
    void editDetails(int ownerId);
    
    void viewProfile(int ownerId) throws DbConnectionException, UserNotFoundException;
    
    void registerOwner(String name, String email, String password, String pan, String aadhaar, String gstin) throws DbConnectionException, UserNotFoundException;
}