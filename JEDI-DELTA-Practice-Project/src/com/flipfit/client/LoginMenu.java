/**
 * ============================================================================
 * 1. INTEGRATED LOGIN MENU (client/LoginMenu.java)
 * ============================================================================
 */
package com.flipfit.client;

import java.util.Scanner;
import com.flipfit.helper.InputValidator;
import com.flipfit.dao.CustomerDAO;
import com.flipfit.dao.OwnerDAO;
import com.flipfit.dao.UserDAO;
import com.flipfit.bean.FlipFitGymOwner;
import com.flipfit.bean.FlipFitCustomer;

public class LoginMenu {
    
    private com.flipfit.business.GymOwnerService gymOwnerService = new com.flipfit.business.GymOwnerServiceImpl();
    
    public int showStartMenu(Scanner sc) {
        System.out.println("\n========== FLIPFIT APPLICATION ==========");
        System.out.println("1. Login");
        System.out.println("2. Register as Gym Owner");
        System.out.println("3. Register as Gym Customer");
        System.out.println("4. Exit");
        System.out.print("Enter your choice: ");
        int choice = InputValidator.readInt(sc);
        
        switch (choice) {
            case 1:
                return login(sc);
            case 2:
                registerOwner(sc);
                return 1;
            case 3:
                registerCustomer(sc);
                return 1;
            case 4:
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
        
        // This service call should insert into 'users' table THEN 'Owner' table
        gymOwnerService.registerOwner(name, email, password, pan, aadhaar, gstin);
        
        System.out.println("\n➤ Gym Owner Registration successful!");
        System.out.println("➤ Note: Your account is pending admin approval.");
    }

    private void registerCustomer(Scanner sc) {
        System.out.println("\n========== CUSTOMER REGISTRATION ==========");
        System.out.print("Enter full name: ");
        sc.nextLine(); // buffer
        String name = sc.nextLine();
        System.out.print("Enter email: ");
        String email = sc.next();
        System.out.print("Enter password: ");
        String password = sc.next();
        System.out.print("Enter Contact Number: ");
        String contact = sc.next();

        // UPDATED: Now passing email and password variables so they aren't hardcoded in DAO
        CustomerDAO customerDAO = CustomerDAO.getInstance();
        FlipFitCustomer customer = customerDAO.addCustomer(name, email, password);
        
        // Update the dummy values with actual registration data
        customer.setContact(contact);
        customerDAO.updateCustomer(customer);
        
        System.out.println("\n➤ Customer registration successful!");
        System.out.println("➤ You can now login with email: " + email);
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
        
        switch (roleChoice) {
            case 1:
                OwnerDAO ownerDAO = OwnerDAO.getInstance();
                FlipFitGymOwner owner = ownerDAO.getOwnerByEmail(email);
                
                if (owner == null) {
                    System.out.println("\n✗ Gym Owner account not found for email: " + email);
                    return 1;
                }
                if (!owner.isApproved()) {
                    System.out.println("\n✗ Your account is still pending admin approval.");
                    return 1;
                }
                
                System.out.println("\n✓ Logged in as Gym Owner: " + owner.getName());
                new GymOwnerMenu().showMenu(sc, owner.getOwnerId());
                break;
                
            case 2:
                CustomerDAO customerDAO = CustomerDAO.getInstance();
                // UPDATED: Search by Email instead of Name to fix login failure
                FlipFitCustomer customer = customerDAO.getCustomerByEmail(email);
                if (customer == null) {
                    System.out.println("\n✗ Customer account not found. Please register.");
                    return 1;
                }
                System.out.println("\n✓ Logged in as Gym Customer: " + customer.getFullName());
                new CustomerMenu().showMenu(sc, customer.getUserId());
                break;
                
            case 3:
                com.flipfit.dao.AdminDAO adminDAO = com.flipfit.dao.AdminDAO.getInstance();
                if (!adminDAO.login(email, password)) {
                    System.out.println("\n✗ Invalid admin credentials");
                    return 1;
                }
                System.out.println("\n✓ Logged in as Gym Admin");
                new AdminMenu().showMenu(sc);
                break;
                
            default:
                System.out.println("Invalid role selected");
                return login(sc);
        }
        
        return showLogoutMenu(sc);
    }
    
    private int showLogoutMenu(Scanner sc) {
        System.out.println("\n===== LOGOUT OPTIONS =====");
        System.out.println("1. Login as different person");
        System.out.println("2. Exit application");
        System.out.print("Enter your choice: ");
        int choice = InputValidator.readInt(sc);
        
        return (choice == 1) ? 1 : 0;
    }
}