package com.flipfit.business;

import com.flipfit.bean.FlipFitGymCenter;
import com.flipfit.bean.FlipFitGymOwner;
import com.flipfit.bean.Slot;
import com.flipfit.bean.Booking;
import com.flipfit.bean.FlipFitCustomer;
import com.flipfit.dao.GymCentreDAO;
import com.flipfit.dao.OwnerDAO;
import com.flipfit.dao.SlotDAO;
import com.flipfit.dao.BookingDAO;
import com.flipfit.exceptions.DbConnectionException;
import com.flipfit.exceptions.UserNotFoundException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class GymOwnerServiceImpl implements GymOwnerService {
    
    private final OwnerDAO ownerDAO = OwnerDAO.getInstance();
    private final GymCentreDAO gymCentreDAO = GymCentreDAO.getInstance();
    private final SlotDAO slotDAO = SlotDAO.getInstance();

    @Override
    public FlipFitGymOwner registerOwner(String name, String pan, String aadhaar, String gstin) throws DbConnectionException, UserNotFoundException {
        // Register owner in DAO
        ownerDAO.addOwner(name);
        
        // This might throw UserNotFoundException if something went wrong immediately after adding
        FlipFitGymOwner registeredOwner = ownerDAO.getOrCreateOwnerByName(name);
        
        // Update the owner details
        System.out.println("\n✓ Registration Successful!");
        System.out.println("  Owner ID: " + registeredOwner.getOwnerId());
        System.out.println("  Name: " + registeredOwner.getName());
        System.out.println("  Status: PENDING APPROVAL");
        System.out.println("\n  Your gym centers will be visible to customers once approved by admin.");
        
        return registeredOwner;
    }

    @Override
    public void addCentre(int ownerId, int centerId, String gymName, String city, String state, int pincode, int capacity) throws DbConnectionException {
        FlipFitGymCenter gymCenter = new FlipFitGymCenter(centerId, gymName, city, state, pincode, capacity);
        gymCenter.setOwnerId(ownerId);
        gymCentreDAO.addGymCentre(gymCenter);
        System.out.println("✓ Gym Centre '" + gymName + "' added successfully for Owner: " + ownerId);
    }

    @Override
    public List<FlipFitGymCenter> viewCentres(int ownerId) throws DbConnectionException {
        List<FlipFitGymCenter> ownerCentres = new ArrayList<>();
        List<FlipFitGymCenter> allCentres = gymCentreDAO.getAllCentres();
        
        for (FlipFitGymCenter centre : allCentres) {
            if (centre.getOwnerId() == ownerId) {
                ownerCentres.add(centre);
            }
        }
        
        if (ownerCentres.isEmpty()) {
            System.out.println("No gym centres found for this owner.");
        } else {
            System.out.println("\n===== YOUR GYM CENTRES =====");
            for (FlipFitGymCenter centre : ownerCentres) {
                System.out.println(centre);
            }
        }
        return ownerCentres;
    }

    @Override
    public void addSlot(int centerId, int slotId, LocalDate date, String startTime, String endTime, int seats) throws DbConnectionException {
        Slot slot = new Slot(slotId, centerId, date, startTime, endTime, seats);
        slotDAO.addSlot(slot);
        System.out.println("✓ Slot added successfully for Center ID: " + centerId);
    }

    @Override
    public void viewSlots(int centerId) throws DbConnectionException {
        List<Slot> slots = slotDAO.getSlotsByCenterId(centerId);
        if (slots.isEmpty()) {
            System.out.println("No slots found for this gym centre.");
        } else {
            System.out.println("\n===== SLOTS FOR CENTER " + centerId + " =====");
            for (Slot slot : slots) {
                System.out.println(slot);
            }
        }
    }

    @Override
    public void viewCustomers(int centreId) throws DbConnectionException, UserNotFoundException {
        System.out.println("\n----- Customers & Bookings for Centre " + centreId + " -----");
        // Find slots for the centre
        List<Slot> slots = slotDAO.getSlotsByCenterId(centreId);
        if (slots.isEmpty()) {
            System.out.println("No slots found for this centre.");
            return;
        }

        // Collect bookings with customer details
        BookingDAO bookingDAO = BookingDAO.getInstance();
        Map<Integer, List<Booking>> customerBookings = new HashMap<>();
        
        for (Slot s : slots) {
            List<Booking> slotBookings = bookingDAO.getBookingsBySlotId(s.getSlotId());
            for (Booking b : slotBookings) {
                customerBookings.computeIfAbsent(b.getUserId(), k -> new ArrayList<>()).add(b);
            }
        }

        if (customerBookings.isEmpty()) {
            System.out.println("No customers have booked slots for this centre yet.");
            return;
        }

        // Lookup customer details and display with booking info
        AdminService adminService = new AdminServiceImpl();
        System.out.println("\n===== CUSTOMER BOOKINGS =====");
        
        for (Integer uid : customerBookings.keySet()) {
            // This now throws UserNotFoundException if ID is invalid
            FlipFitCustomer customer = adminService.getCustomerById(uid);
            
            System.out.println("\nCustomer: " + customer.getFullName() + " (ID: " + uid + ", Contact: " + customer.getContact() + ")");
            for (Booking booking : customerBookings.get(uid)) {
                Slot slot = slotDAO.getSlotById(booking.getUserId(), booking.getSlotId(), booking.getCenterId());
                if (slot != null) {
                    String dateStr = (slot.getDate() != null) ? slot.getDate().toString() : "N/A";
                    System.out.println("  - Booking #" + booking.getBookingId() + ": Slot " + slot.getSlotId() + " on " + dateStr + " at " + slot.getStartTime() + "-" + slot.getEndTime());
                } else {
                    System.out.println("  - Booking #" + booking.getBookingId() + ": Slot " + booking.getSlotId());
                }
            }
        }
    }

    @Override
    public void viewPayments(int ownerId) {
        System.out.println("Displaying payment history...");
    }

    @Override
    public void editDetails(int ownerId) {
        System.out.println("Gym Owner details updated.");
    }

    @Override
    public void viewProfile(int ownerId) throws DbConnectionException, UserNotFoundException {
        // This line now throws exception if owner is null, removing the need for null check
        FlipFitGymOwner owner = ownerDAO.getOwnerById(ownerId);

        System.out.println("\n===== OWNER PROFILE =====");
        System.out.println("Owner ID: " + owner.getOwnerId());
        System.out.println("Name: " + owner.getName());
        System.out.println("PAN: " + owner.getPan());
        System.out.println("Aadhaar: " + owner.getAadhaar());
        System.out.println("GSTIN: " + owner.getGstin());
        System.out.println("Validated: " + (owner.isValidated() ? "✓ YES" : "✗ NO"));
        System.out.println("Approved: " + (owner.isApproved() ? "✓ YES - Visible to Customers" : "✗ NO - Pending Admin Approval"));
        
        // Show centres count
        List<FlipFitGymCenter> centres = viewCentres(ownerId);
        System.out.println("Total Centres: " + centres.size());
    }
    
    @Override
    public void registerOwner(String name, String email, String password, String pan, String aadhaar, String gstin) throws DbConnectionException, UserNotFoundException {
        // 1. Create the base User entry
        ownerDAO.addOwner(name, email, password); 

        // 2. Fetch the ID generated for this user
        // This now throws exception if not found, removing need for null check
        FlipFitGymOwner owner = ownerDAO.getOwnerByName(name);
        
        // 3. Add professional details
        ownerDAO.addOwnerDetails(owner.getOwnerId(), pan, aadhaar, gstin);
    }
}