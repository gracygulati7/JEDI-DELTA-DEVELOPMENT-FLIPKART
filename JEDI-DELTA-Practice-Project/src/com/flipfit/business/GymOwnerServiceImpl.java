package com.flipfit.business;
import com.flipfit.bean.TimeFrame;
import com.flipfit.bean.FlipFitGymCenter;
import com.flipfit.bean.Slot;
import java.time.LocalDate;
import com.flipfit.bean.Booking;
import com.flipfit.dao.GymCentreDAO;
import com.flipfit.dao.SlotDAO;
import com.flipfit.dao.BookingDAO;
import com.flipfit.business.AdminService;
import com.flipfit.business.AdminServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GymOwnerServiceImpl implements GymOwnerService {
    
    private final GymCentreDAO gymCentreDAO = GymCentreDAO.getInstance();
    private final SlotDAO slotDAO = SlotDAO.getInstance();

    @Override
    public void addCentre(int ownerId, int centerId, String gymName, String city, String state, int pincode, int capacity) {
        FlipFitGymCenter gymCenter = new FlipFitGymCenter(centerId, gymName, city, state, pincode, capacity);
        gymCenter.setOwnerId(ownerId);
        gymCentreDAO.addGymCentre(gymCenter);
        System.out.println("✓ Gym Centre '" + gymName + "' added successfully for Owner: " + ownerId);
    }

    @Override
    public List<FlipFitGymCenter> viewCentres(int ownerId) {
        List<FlipFitGymCenter> ownerCentres = new ArrayList<>();
        List<FlipFitGymCenter> allCentres = gymCentreDAO.getGymCentres();
        
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
    public void addSlot(int centerId, int slotId, LocalDate date, String startTime,String endTime,int seats) {
        Slot slot = new Slot(slotId, centerId, date, startTime,endTime, seats);
        slotDAO.addSlot(slot);
        System.out.println("✓ Slot added successfully for Center ID: " + centerId);
    }

    @Override
    public void viewSlots(int centerId) {
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
    public void viewCustomers(int centreId) {
        System.out.println("\n----- Customers & Bookings for Centre " + centreId + " -----");
        // Find slots for the centre
        List<Slot> slots = slotDAO.getSlotsByCenterId(centreId);
        if (slots.isEmpty()) {
            System.out.println("No slots found for this centre.");
            return;
        }

        // Collect bookings with customer details
        BookingDAO bookingDAO = BookingDAO.getInstance();
        java.util.Map<Integer, java.util.List<Booking>> customerBookings = new java.util.HashMap<>();
        
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
            com.flipfit.bean.FlipFitCustomer customer = adminService.getCustomerById(uid);
            if (customer != null) {
                System.out.println("\nCustomer: " + customer.getFullName() + " (ID: " + uid + ", Contact: " + customer.getContact() + ")");
                for (Booking booking : customerBookings.get(uid)) {
                    Slot slot = slotDAO.getSlotById(booking.getUserId(),booking.getSlotId(),booking.getCenterId());
                    if (slot != null) {
                        String dateStr = (slot.getDate() != null) ? slot.getDate().toString() : "N/A";
                        System.out.println("  - Booking #" + booking.getBookingId() + ": Slot " + slot.getSlotId() + " on " + dateStr + " at " + slot.getStartTime() + "-"+slot.getEndTime());
                    } else {
                        System.out.println("  - Booking #" + booking.getBookingId() + ": Slot " + booking.getSlotId());
                    }
                }
            } else {
                System.out.println("\nCustomer ID: " + uid + " (details not found)");
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
    public void viewProfile(int ownerId) {
        com.flipfit.dao.OwnerDAO ownerDAO = com.flipfit.dao.OwnerDAO.getInstance();
        com.flipfit.bean.FlipFitGymOwner owner = ownerDAO.getOwnerById(ownerId);
        if (owner != null) {
            System.out.println("\n===== OWNER PROFILE =====");
            System.out.println("Owner ID: " + owner.getOwnerId());
            System.out.println("Name: " + owner.getName());
            System.out.println("PAN: " + owner.getPan());
            System.out.println("Aadhaar: " + owner.getAadhaar());
            System.out.println("GSTIN: " + owner.getGstin());
            System.out.println("Validated: " + owner.isValidated());
            
            // Show centres count
            List<FlipFitGymCenter> centres = viewCentres(ownerId);
            System.out.println("Total Centres: " + centres.size());
        } else {
            System.out.println("Owner not found.");
        }
    }
}
