package com.flipfit.client;
import java.util.Scanner;
import com.flipfit.helper.InputValidator;

public class LoginMenu {
    
    // Instantiate service here or in constructor
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
        
        // ADDED EMAIL AND PASSWORD INPUTS
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
        
        // Updated service call with 6 parameters
        gymOwnerService.registerOwner(name, email, password, pan, aadhaar, gstin);
        
        System.out.println("\n➤ Registration successful!");
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
            // Important: Logic should ideally verify password here too
            com.flipfit.dao.OwnerDAO ownerDAO = com.flipfit.dao.OwnerDAO.getInstance();
            // Since we use email for login, update your DAO to getOwnerByEmail 
            // Or use getOwnerByName if you still want to use 'full_name' as username
            com.flipfit.bean.FlipFitGymOwner owner = ownerDAO.getOwnerByName(email); 
            
            if (owner == null) {
                System.out.println("\n✗ Gym Owner account not found.");
                return 1;
            }
            
            // Note: In a real app, you'd check password here: if(!owner.getPassword().equals(password))...

            if (!owner.isApproved()) {
                System.out.println("\n✗ Your account is still pending admin approval.");
                return 1;
            }
            
            System.out.println("\n✓ Logged in as Gym Owner: " + owner.getName());
            GymOwnerMenu gymOwnerMenu = new GymOwnerMenu();
            gymOwnerMenu.showMenu(sc, owner.getOwnerId());
            break;
            
        case 2:
            // Similar logic for Customer using customerDAO
            System.out.println("\n✓ Logged in as Gym Customer");
            com.flipfit.dao.CustomerDAO customerDAO = com.flipfit.dao.CustomerDAO.getInstance();
            com.flipfit.bean.FlipFitCustomer customer = customerDAO.getOrCreateCustomerByName(email);
            CustomerMenu customerMenu = new CustomerMenu();
            customerMenu.showMenu(sc, customer.getUserId());
            break;
            
        case 3:
            com.flipfit.dao.AdminDAO adminDAO = com.flipfit.dao.AdminDAO.getInstance();
            boolean isValidAdmin = adminDAO.login(email, password);
            if (!isValidAdmin) {
                System.out.println("\n✗ Invalid admin credentials");
                return 1;
            }
            System.out.println("\n✓ Logged in as Gym Admin");
            AdminMenu adminMenu = new AdminMenu();
            adminMenu.showMenu(sc);
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