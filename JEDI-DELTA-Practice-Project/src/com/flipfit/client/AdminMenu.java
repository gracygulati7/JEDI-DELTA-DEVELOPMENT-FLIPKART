package com.flipfit.client;

import java.util.Scanner;
import java.util.List;
import com.flipfit.business.AdminService;
import com.flipfit.business.AdminServiceImpl;
import com.flipfit.business.UserService;
import com.flipfit.business.UserServiceImpl; 
import com.flipfit.helper.InputValidator;
import com.flipfit.dao.GymCentreDAO;
import com.flipfit.dao.SlotDAO;
import com.flipfit.bean.FlipFitGymCenter;
import com.flipfit.bean.Slot;

public class AdminMenu {

    private AdminService adminService = new AdminServiceImpl();
    private final UserService userService = new UserServiceImpl(); 
    private final GymCentreDAO gymCentreDAO = GymCentreDAO.getInstance();
    private final SlotDAO slotDAO = SlotDAO.getInstance();

    public void showMenu(Scanner sc) {

        int choice;

        do {
            System.out.println("\n===== ADMIN MENU =====");
            System.out.println("1. Validate Gym Owner");
            System.out.println("2. Delete Gym Owner");
            System.out.println("3. View FlipFit Customers");
            System.out.println("4. Add Gym Center");
            System.out.println("5. View Gym Centers");
            System.out.println("6. Add Slot Info");
            System.out.println("7. View Slots");
            System.out.println("8. View Available Slots"); 
            System.out.println("9. View Profile");
            System.out.println("10. Edit Profile");
            System.out.println("0. Logout");

            System.out.print("Enter choice: ");
            choice = InputValidator.readInt(sc);

            switch (choice) {
                case 1:
                    System.out.print("Enter Owner ID: ");
                    int ownerId = InputValidator.readInt(sc);
                    adminService.validateOwner(ownerId);
                    break;

                case 2:
                    System.out.print("Enter Owner ID: ");
                    ownerId = InputValidator.readInt(sc);
                    adminService.deleteOwner(ownerId);
                    break;

                case 3:
                    adminService.viewFFCustomers();
                    break;

                case 4:
                    System.out.print("Center ID: ");
                    int centerId = InputValidator.readInt(sc);
                    System.out.print("Gym Name: ");
                    String gymName = sc.next();
                    System.out.print("City: ");
                    String city = sc.next();
                    System.out.print("State: ");
                    String state = sc.next();
                    System.out.print("Pincode: ");
                    int pincode = InputValidator.readInt(sc);
                    System.out.print("Capacity: ");
                    int capacity = InputValidator.readInt(sc);

                    adminService.addGymCenter(centerId, gymName, city, state, pincode, capacity);
                    break;

                case 5:
                    viewGymCentersWithSlots();
                    break;

                case 6:
                    System.out.print("Center ID: ");
                    centerId = InputValidator.readInt(sc);
                    System.out.print("Slot ID: ");
                    int slotId = InputValidator.readInt(sc);
                    System.out.print("Start Time (HH:MM format, e.g., 5:30, 14:45): ");
                    String startTime = sc.next();
                    System.out.print("End Time (HH:MM format, e.g., 6:30, 15:45): ");
                    String endTime = sc.next();
                    System.out.print("Seat Capacity: ");
                    int seats = InputValidator.readInt(sc);

                    adminService.addSlotInfo(centerId, slotId, startTime, endTime, seats);
                    break;

                case 7:
                    System.out.print("Center ID: ");
                    centerId = InputValidator.readInt(sc);
                    adminService.viewSlots(centerId);
                    break;

                case 8:
                    viewAvailableSlotsUserService(); 
                    break;
                    
                case 9:
                    System.out.print("Enter your Admin ID: ");
                    int adminId = InputValidator.readInt(sc);
                    userService.viewProfile(adminId);
                    break;

                case 10:
                    System.out.print("Enter your Admin ID: ");
                    adminId = InputValidator.readInt(sc);
                    userService.editProfile(adminId);
                    break;

                case 0:
                    System.out.println("Logging out...");
                    break;

                default:
                    System.out.println("Invalid choice!");
            }
        } while (choice != 0);
    }

    private void viewGymCentersWithSlots() {
        System.out.println("\n===== ALL GYM CENTERS WITH SLOTS =====");
        List<FlipFitGymCenter> centers = gymCentreDAO.getGymCentres();

        if (centers.isEmpty()) {
            System.out.println("No gym centers found.");
            return;
        }

        for (FlipFitGymCenter center : centers) {
            System.out.println("\n" + center);

            // Get and display slots for this center
            List<Slot> slots = slotDAO.getSlotsByCenterId(center.getGymId());
            if (slots.isEmpty()) {
                System.out.println("  └─ No slots available for this center");
            } else {
                System.out.println("  └─ Slots:");
                for (Slot slot : slots) {
                    String dateStr = (slot.getDate() != null) ? slot.getDate().toString() : "N/A";
                    System.out.println("     • Slot ID: " + slot.getSlotId() +
                            " | Date: " + dateStr +
                            " | Time: " + slot.getStartTime() + " - " + slot.getEndTime() +
                            " | Available Seats: " + slot.getSeatsAvailable());
                }
            }
        }
    }

    // ---method using UserService ---
    private void viewAvailableSlotsUserService() {
        System.out.println("\n===== ALL AVAILABLE SLOTS (via UserService) =====");
        List<FlipFitGymCenter> centers = gymCentreDAO.getGymCentres();

        if (centers.isEmpty()) {
            System.out.println("No gym centers available.");
            return;
        }

        for (FlipFitGymCenter center : centers) {
            System.out.println("\nGym: " + center.getGymName() + " (ID: " + center.getGymId() + ")");
            List<Slot> slots = userService.findAvailableSlots(center.getGymId());
            if (slots.isEmpty()) {
                System.out.println("  └─ No available slots");
            } else {
                for (Slot slot : slots) {
                    System.out.println("     • Slot ID: " + slot.getSlotId() +
                            " | Date: " + slot.getDate() +
                            " | Time: " + slot.getStartTime() + " - " + slot.getEndTime() +
                            " | Seats: " + slot.getSeatsAvailable() + "/" + slot.getTotalSeats());
                }
            }
        }
    }
}
