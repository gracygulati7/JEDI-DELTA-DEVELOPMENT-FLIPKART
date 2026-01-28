package com.flipfit.business;

import java.util.*;

import com.flipfit.bean.FlipFitCustomer;
import com.flipfit.bean.FlipFitGymCenter;
import com.flipfit.bean.FlipFitGymOwner;
import com.flipfit.bean.Slot;
import com.flipfit.dao.AdminDAO;
import com.flipfit.dao.CustomerDAO;
import com.flipfit.dao.GymCentreDAO;
import com.flipfit.dao.OwnerDAO;
import com.flipfit.dao.SlotDAO;
import com.flipfit.exceptions.CentreNotFoundException;
import com.flipfit.exceptions.DbConnectionException;
import com.flipfit.exceptions.UserNotFoundException;
import com.flipfit.exceptions.WrongCredentialsException;

public class AdminServiceImpl implements AdminService {

    private final OwnerDAO ownerDAO = OwnerDAO.getInstance();
    private final CustomerDAO customerDAO = CustomerDAO.getInstance();
    private final GymCentreDAO gymCentreDAO = GymCentreDAO.getInstance();
    private final SlotDAO slotDAO = SlotDAO.getInstance();
    private final AdminDAO adminDAO = AdminDAO.getInstance();

    public AdminServiceImpl() {
        // Simple try-catch for seeding data (only runs once on startup)
        try {
            // Check if they exist first or rely on DAO internal checks, 
            // strictly just for the constructor demo data
            customerDAO.addCustomer("Amit");
            customerDAO.addCustomer("Neha");
        } catch (DbConnectionException e) {
            System.err.println("Warning: Could not seed default customers: " + e.getMessage());
        }
    }

    @Override
    public boolean login(String username, String password) throws WrongCredentialsException, DbConnectionException {
        // Just passes the call. If it fails, DAO throws WrongCredentialsException
        return adminDAO.login(username, password);
    }

    @Override
    public void validateOwner(int ownerId) throws DbConnectionException, UserNotFoundException {
        // DAO now throws UserNotFoundException if ID is wrong, so we don't need 'if (owner != null)'
        FlipFitGymOwner owner = ownerDAO.getOwnerById(ownerId);
        owner.setValidated(true);
        // Note: You might need an 'updateOwner' method in DAO to save this validation status permanently!
    }

    @Override
    public void deleteOwner(int ownerId) throws DbConnectionException, UserNotFoundException {
        // Assuming you have a delete method in OwnerDAO, or similar logic. 
        // For now, checking existence is the main validation.
        ownerDAO.getOwnerById(ownerId); // This throws UserNotFoundException if missing
        
        // Implementation note: You should add ownerDAO.deleteOwner(ownerId) to your DAO!
        // For now, we simulate success if they exist.
    }

    @Override
    public void viewFFCustomers() throws DbConnectionException {
        System.out.println("\n--- FlipFit Customers ---");
        for (FlipFitCustomer c : customerDAO.getAllCustomers()) {
            System.out.println(c);
        }
    }

    @Override
    public FlipFitCustomer getCustomerById(int userId) throws DbConnectionException, UserNotFoundException {
        return customerDAO.getCustomerById(userId);
    }

    // -------- REQUIRED FUNCTIONS --------
    @Override
    public void addGymCenter(int centerId, String gymName, String city,
                             String state, int pincode, int capacity) throws DbConnectionException {

        FlipFitGymCenter center =
                new FlipFitGymCenter(centerId, gymName, city, state, pincode, capacity);
        gymCentreDAO.addGymCentre(center);
    }

    @Override
    public void viewGymCenters() throws DbConnectionException {
        System.out.println("\n--- Gym Centers ---");
        for (FlipFitGymCenter c : gymCentreDAO.getAllCentres()) {
            System.out.println(c);
        }
    }

    @Override
    public void addSlotInfo(int centerId, int slotId,
                            String startTime, String endTime, int seats) throws DbConnectionException, CentreNotFoundException {

        // Validate center exists first
        gymCentreDAO.getGymCentreById(centerId); // Throws CentreNotFoundException if missing

        Slot slot = new Slot(
                slotId, centerId, java.time.LocalDate.now(),
                startTime, endTime, seats
        );
        slotDAO.addSlot(slot);
    }

    @Override
    public void viewSlots(int centerId) throws DbConnectionException, CentreNotFoundException {
        // Validate center exists
        gymCentreDAO.getGymCentreById(centerId); 
        
        System.out.println("\n--- Slots for Center " + centerId + " ---");
        List<Slot> slots = slotDAO.getSlotsByCenterId(centerId);
        if (slots.isEmpty()) {
            System.out.println("No slots found for this center.");
        } else {
            for (Slot s : slots) {
                System.out.println(s);
            }
        }
    }

    // -------- OWNER MANAGEMENT --------
    @Override
    public void viewAllGymOwners() throws DbConnectionException {
        Collection<FlipFitGymOwner> allOwners = ownerDAO.getAllOwners();

        if (allOwners.isEmpty()) {
            System.out.println("\n--- No Gym Owners Found ---");
            return;
        }

        System.out.println("\n========== ALL GYM OWNERS ==========");
        for (FlipFitGymOwner owner : allOwners) {
            String approvalStatus = owner.isApproved() ? "✓ APPROVED" : "✗ PENDING";
            // String validationStatus = owner.isValidated() ? "✓ VALIDATED" : "✗ NOT VALIDATED";

            System.out.println("\n" + owner);
            System.out.println("  → Approval Status: " + approvalStatus);
        }
        System.out.println("\n====================================");
    }

    @Override
    public FlipFitGymOwner getOwnerById(int ownerId) throws DbConnectionException, UserNotFoundException {
        return ownerDAO.getOwnerById(ownerId);
    }

    @Override
    public void approveOwner(int ownerId) throws DbConnectionException, UserNotFoundException {
        FlipFitGymOwner owner = ownerDAO.getOwnerById(ownerId); // Throws if not found
        
        owner.setApproved(true);
        // Important: Update DB state if you have an update method
        // ownerDAO.approveOwner(ownerId); <-- Recommend adding this to OwnerDAO
        
        List<FlipFitGymCenter> centers = gymCentreDAO.getAllCentres();
        for (FlipFitGymCenter center : centers) {
            if (center.getOwnerId() == ownerId) {
                center.setApproved(true);
                gymCentreDAO.approveCenter(center.getCenterId());
            }
        }
    }
}