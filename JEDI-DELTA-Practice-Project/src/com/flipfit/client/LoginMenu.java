package com.flipfit.client;

import java.util.Scanner;
import com.flipfit.helper.InputValidator;
import com.flipfit.exceptions.*;

public class LoginMenu {
    
    private com.flipfit.business.GymOwnerService gymOwnerService = new com.flipfit.business.GymOwnerServiceImpl();
    
    public int showStartMenu(Scanner sc) {
        System.out.println("\n========== FLIPFIT APPLICATION ==========");
        System.out.println("1. Login");
        System.out.println("2. Register as Gym Owner");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");
        int choice = InputValidator.readInt(sc);
        
        switch (choice) {
        case 1:
            return login(sc);
        case 2:
            registerOwner(sc);
            return 1;  
        case 3:
            System.out.println("\n--- Exiting application ---");
            return 0;
        default:
            System.out.println("Invalid choice! Please try again.");
            return showStartMenu(sc);
        }
    }
    
    private void registerOwner(Scanner sc) {
        System.out.println("\n========== GYM OWNER REGISTRATION ==========");
        System.out.print("Enter full name: ");
        sc.nextLine(); // Clear buffer
        String name = sc.nextLine();
        
        System.out.print("Enter email: ");
        String email = sc.next();
        System.out.print("Enter password: ");
        String password = sc.next();
        
        System.out.print("Enter PAN number: ");
        String pan = sc.next();
        System.out.print("Enter Aadhaar number: ");
        String aadhaar = sc.next();
        System.out.print("Enter GSTIN: ");
        String gstin = sc.next();
        
        try {
            gymOwnerService.registerOwner(name, email, password, pan, aadhaar, gstin);
            System.out.println("\n➤ Registration successful!");
            System.out.println("➤ You can now login with email: " + email);
        } catch (DbConnectionException e) {
            System.out.println("Error: Registration failed due to system error. " + e.getMessage());
        } catch (UserNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    public int login(Scanner sc) {
        System.out.println("\n===== FLIPFIT LOGIN =====");
        System.out.print("Email (Username): ");
        String email = sc.next();
        System.out.print("Password: ");
        String password = sc.next(); 
        
        System.out.println("\nSelect Role:");
        System.out.println("1. Gym Owner");
        System.out.println("2. Gym Customer");
        System.out.println("3. Gym Admin");
        System.out.print("Enter your choice: ");
        int roleChoice = InputValidator.readInt(sc);
        
        try {
            switch (roleChoice) {
            case 1:
                com.flipfit.dao.OwnerDAO ownerDAO = com.flipfit.dao.OwnerDAO.getInstance();
                try {
                    com.flipfit.bean.FlipFitGymOwner owner = ownerDAO.getOwnerByName(email);
                    
                    // In a real app, verify password here
                    if (!owner.isApproved()) {
                        System.out.println("\n✗ Your account is still pending admin approval.");
                        return 1;
                    }
                    
                    System.out.println("\n✓ Logged in as Gym Owner: " + owner.getName());
                    GymOwnerMenu gymOwnerMenu = new GymOwnerMenu();
                    gymOwnerMenu.showMenu(sc, owner.getOwnerId());
                    
                } catch (UserNotFoundException e) {
                    System.out.println("\n✗ Gym Owner account not found.");
                    return 1;
                }
                break;
                
            case 2:
                System.out.println("\n✓ Logged in as Gym Customer");
                com.flipfit.dao.CustomerDAO customerDAO = com.flipfit.dao.CustomerDAO.getInstance();
                // getOrCreate might throw DB exception
                com.flipfit.bean.FlipFitCustomer customer = customerDAO.getOrCreateCustomerByName(email);
                CustomerMenu customerMenu = new CustomerMenu();
                customerMenu.showMenu(sc, customer.getUserId());
                break;
                
            case 3:
                com.flipfit.dao.AdminDAO adminDAO = com.flipfit.dao.AdminDAO.getInstance();
                try {
                    boolean isValidAdmin = adminDAO.login(email, password);
                    if (!isValidAdmin) {
                        System.out.println("\n✗ Invalid admin credentials");
                        return 1;
                    }
                    System.out.println("\n✓ Logged in as Gym Admin");
                    AdminMenu adminMenu = new AdminMenu();
                    adminMenu.showMenu(sc);
                } catch (WrongCredentialsException e) {
                    System.out.println("\n✗ " + e.getMessage());
                    return 1;
                }
                break;
                
            default:
                System.out.println("Invalid role selected");
                return login(sc);
            }
        } catch (DbConnectionException e) {
            System.out.println("System Error: Unable to connect to database. " + e.getMessage());
            return 1;
        }
        
        return showLogoutMenu(sc);
    }
    
    private int showLogoutMenu(Scanner sc) {
        System.out.println("\n===== LOGOUT OPTIONS =====");
        System.out.println("1. Login as different person");
        System.out.println("2. Exit application");
        System.out.print("Enter your choice: ");
        int choice = InputValidator.readInt(sc);
        
        switch (choice) {
        case 1:
            return 1; 
        case 2:
            return 0; 
        default:
            System.out.println("Invalid choice!");
            return showLogoutMenu(sc);
        }
    }
}