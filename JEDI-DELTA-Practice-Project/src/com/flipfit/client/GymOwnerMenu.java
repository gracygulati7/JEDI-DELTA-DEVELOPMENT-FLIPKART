package com.flipfit.client;

import java.util.Scanner;
import com.flipfit.business.GymOwnerService;
import com.flipfit.business.GymOwnerServiceImpl;
import com.flipfit.helper.InputValidator;
import com.flipfit.dao.GymCentreDAO;
import com.flipfit.dao.SlotDAO;

public class GymOwnerMenu {

    private GymOwnerService gymOwnerService = new GymOwnerServiceImpl();
    private final GymCentreDAO gymCentreDAO = GymCentreDAO.getInstance();
    private final SlotDAO slotDAO = SlotDAO.getInstance();

    public void showMenu(Scanner sc, int ownerId) {
        int choice;
        do {
            System.out.println("\n===== GYM OWNER MENU =====");
            System.out.println("1. Add Gym Centre");
            System.out.println("2. View My Centres");
            System.out.println("3. Add Slot to Centre");
            System.out.println("4. View Slots in Centre");
            System.out.println("5. View Customers");
            System.out.println("6. View My Profile");
            System.out.println("7. Edit Profile");
            System.out.println("0. Logout");
            System.out.print("Enter your choice: ");
            choice = InputValidator.readInt(sc);

            switch (choice) {
            case 1:
                addGymCentre(sc, ownerId);
                break;
            case 2:
                gymOwnerService.viewCentres(ownerId);
                break;
            case 3:
                addSlot(sc, ownerId);
                break;
            case 4:
                viewSlots(sc);
                break;
            case 5:
                viewCustomers(sc);
                break;
            case 6:
                gymOwnerService.viewProfile(ownerId);
                break;
            case 7:
                gymOwnerService.editDetails(ownerId);
                break;
            case 0:
                System.out.println("Logging out from Gym Owner Menu...");
                break;
            default:
                System.out.println("Invalid choice! Please try again.");
            }
        } while (choice != 0);
    }

    private void addGymCentre(Scanner sc, int ownerId) {
        System.out.println("\n----- Add New Gym Centre -----");
        // Auto-generate centre ID
        int centerId = gymCentreDAO.getNextCentreId();
        System.out.println("Centre ID (Auto-generated): " + centerId);
        
        System.out.print("Gym Name: ");
        String gymName = sc.next();
        System.out.print("City: ");
        String city = sc.next();
        System.out.print("State: ");
        String state = sc.next();
        System.out.print("Pincode: ");
        int pincode = InputValidator.readInt(sc);
        System.out.print("Capacity (number of members): ");
        int capacity = InputValidator.readInt(sc);

        gymOwnerService.addCentre(ownerId, centerId, gymName, city, state, pincode, capacity);
    }

    private void addSlot(Scanner sc, int ownerId) {
        System.out.println("\n----- Add New Slot -----");
        System.out.print("Centre ID: ");
        int centerId = InputValidator.readInt(sc);
        
        if (!gymCentreDAO.centreIdExists(centerId)) {
            System.out.println("❌ Error: Gym Centre with ID " + centerId + " not found!");
            return;
        }
        
        // Auto-generate slot ID
        int slotId = slotDAO.getNextSlotId();
        System.out.println("Slot ID (Auto-generated): " + slotId);
        
        System.out.print("Slot Date (YYYY-MM-DD format, e.g., 2026-01-25): ");
        String dateStr = sc.next();
        java.time.LocalDate date;
        try {
            date = java.time.LocalDate.parse(dateStr);
        } catch (java.time.format.DateTimeParseException e) {
            System.out.println("❌ Error: Invalid date format! Please use YYYY-MM-DD.");
            return;
        }
        
        System.out.print("Start Time (HH:MM format, e.g., 5:30, 14:45): ");
        String startTime = sc.next();
        System.out.print("End Time (HH:MM format, e.g., 6:30, 15:45): ");
        String endTime = sc.next();
        System.out.print("Number of Seats Available: ");
        int seats = InputValidator.readInt(sc);

        gymOwnerService.addSlot(centerId, slotId, date, startTime, endTime, seats);
    }

    private void viewSlots(Scanner sc) {
        System.out.println("\n----- View Slots -----");
        System.out.print("Enter Centre ID: ");
        int centerId = InputValidator.readInt(sc);
        gymOwnerService.viewSlots(centerId);
    }

    private void viewCustomers(Scanner sc) {
        System.out.println("\n----- View Customers -----");
        System.out.print("Enter Centre ID: ");
        int centreId = InputValidator.readInt(sc);
        gymOwnerService.viewCustomers(centreId);
    }

}