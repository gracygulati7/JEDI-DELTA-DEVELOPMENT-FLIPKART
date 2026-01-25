package com.flipfit.business;

import java.util.*;

import com.flipfit.bean.FlipFitCustomer;
import com.flipfit.bean.FlipFitGymCenter;
import com.flipfit.bean.FlipFitGymOwner;
import com.flipfit.bean.Slot;
import com.flipfit.dao.CustomerDAO;
import com.flipfit.dao.GymCentreDAO;
import com.flipfit.dao.SlotDAO;

public class AdminServiceImpl implements AdminService {

    // HARD-CODED DATA STORES (Collections API)
    private Map<Integer, FlipFitGymOwner> owners = new HashMap<>();
    private final CustomerDAO customerDAO = CustomerDAO.getInstance();
    private final GymCentreDAO gymCentreDAO = GymCentreDAO.getInstance();
    private final SlotDAO slotDAO = SlotDAO.getInstance();

    public AdminServiceImpl() {
        // Owners
        owners.put(1, new FlipFitGymOwner(1, "PAN1", "AAD1", "GST1", null));
        owners.put(2, new FlipFitGymOwner(2, "PAN2", "AAD2", "GST2", null));

        // Seed customers into CustomerDAO
        customerDAO.addCustomer("Amit");
        customerDAO.addCustomer("Neha");
    }

    // -------- Diagram Functions --------

    @Override
    public void login() {
        System.out.println("Admin logged in successfully");
    }

    @Override
    public void validateOwner(int ownerId) {
        FlipFitGymOwner owner = owners.get(ownerId);
        if (owner != null) {
            owner.setValidated(true);
            System.out.println("Owner validated");
        } else {
            System.out.println("Owner not found");
        }
    }

    @Override
    public void deleteOwner(int ownerId) {
        owners.remove(ownerId);
        System.out.println("Owner deleted");
    }

    @Override
    public void viewFFCustomers() {
        System.out.println("\n--- FlipFit Customers ---");
        for (FlipFitCustomer c : customerDAO.getAllCustomers()) {
            System.out.println(c);
        }
    }

    @Override
    public FlipFitCustomer getCustomerById(int userId) {
        return customerDAO.getCustomerById(userId);
    }

    // -------- REQUIRED FUNCTIONS --------

    @Override
    public void addGymCenter(int centerId, String gymName, String city, String state,
                             int pincode, int capacity) {

        FlipFitGymCenter center =
                new FlipFitGymCenter(centerId, gymName, city, state, pincode, capacity);
        gymCentreDAO.addGymCentre(center);
        System.out.println("Gym Center added");
    }

    @Override
    public void viewGymCenters() {
        System.out.println("\n--- Gym Centers ---");
        for (FlipFitGymCenter c : gymCentreDAO.getGymCentres()) {
            System.out.println(c);
        }
    }

    @Override
    public void addSlotInfo(int centerId, int slotId,
                            String startTime, String endTime, int seats) {

        FlipFitGymCenter center = gymCentreDAO.getGymCentreById(centerId);
        if (center != null) {
            Slot slot = new Slot(slotId, centerId, java.time.LocalDate.now(), startTime, endTime, seats);
            slotDAO.addSlot(slot);
            System.out.println("Slot added");
        } else {
            System.out.println("Center not found");
        }
    }

    @Override
    public void viewSlots(int centerId) {
        FlipFitGymCenter center = gymCentreDAO.getGymCentreById(centerId);
        if (center != null) {
            System.out.println("\n--- Slots for Center " + centerId + " ---");
            List<Slot> slots = slotDAO.getSlotsByCenterId(centerId);
            if (slots.isEmpty()) {
                System.out.println("No slots found for this center.");
            } else {
                for (Slot s : slots) {
                    System.out.println(s);
                }
            }
        } else {
            System.out.println("Center not found");
        }
    }
}