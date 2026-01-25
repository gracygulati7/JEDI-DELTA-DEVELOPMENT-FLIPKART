package com.flipfit.client;

import java.util.Scanner;
import com.flipfit.business.AdminService;
import com.flipfit.business.AdminServiceImpl;

public class AdminMenu {

    private AdminService adminService = new AdminServiceImpl();

    public void showMenu() {

        try (Scanner sc = new Scanner(System.in)) {
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
                System.out.println("0. Logout");

                System.out.print("Enter choice: ");
                choice = sc.nextInt();
                switch (choice) {
                case 1:
                    System.out.print("Enter Owner ID: ");
                    int ownerId = sc.nextInt();
                    adminService.validateOwner(ownerId);
                    break;

                case 2:
                    System.out.print("Enter Owner ID: ");
                    ownerId = sc.nextInt();
                    adminService.deleteOwner(ownerId);
                    break;

                case 3:
                    adminService.viewFFCustomers();
                    break;

                case 4:
                    System.out.print("Center ID: ");
                    int centerId = sc.nextInt();
                    System.out.print("Gym Name: ");
                    String gymName = sc.next();
                    System.out.print("City: ");
                    String city = sc.next();
                    System.out.print("State: ");
                    String state = sc.next();
                    System.out.print("Pincode: ");
                    int pincode = sc.nextInt();
                    System.out.print("Capacity: ");
                    int capacity = sc.nextInt();

                    adminService.addGymCenter(centerId, gymName, city, state, pincode, capacity);
                    break;

                case 5:
                    adminService.viewGymCenters();
                    break;

                case 6:
                    System.out.print("Center ID: ");
                    centerId = sc.nextInt();
                    System.out.print("Slot ID: ");
                    int slotId = sc.nextInt();
                    System.out.print("Start Time: ");
                    int startTime = sc.nextInt();
                    System.out.print("Seat Capacity: ");
                    int seats = sc.nextInt();

                    adminService.addSlotInfo(centerId, slotId, startTime, seats);
                    break;

                case 7:
                    System.out.print("Center ID: ");
                    centerId = sc.nextInt();
                    adminService.viewSlots(centerId);
                    break;

                case 0:
                    System.out.println("Logging out...");
                    break;

                default:
                    System.out.println("Invalid choice!");
                }
            } while (choice != 0);
        }
    }
}