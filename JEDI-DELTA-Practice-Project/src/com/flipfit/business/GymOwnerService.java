package com.flipfit.business;
import com.flipfit.bean.TimeFrame;
import com.flipfit.bean.FlipFitGymCenter;
import java.util.List;

public interface GymOwnerService {
    void addCentre(int ownerId, int centerId, String gymName, String city, String state, int pincode, int capacity);
    List<FlipFitGymCenter> viewCentres(int ownerId);
    void addSlot(int centerId, int slotId, java.time.LocalDate date, String startTime,String endTime, int seats);
    void viewSlots(int centerId);
    void viewCustomers(int centreId);
    void viewPayments(int ownerId);
    void editDetails(int ownerId);
    void viewProfile(int ownerId);
}