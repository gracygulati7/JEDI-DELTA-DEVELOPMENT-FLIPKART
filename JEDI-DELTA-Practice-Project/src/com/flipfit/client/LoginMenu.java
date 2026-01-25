package com.flipfit.client;
import java.util.Scanner;
import com.flipfit.helper.InputValidator;

public class LoginMenu {
	
	public int login(Scanner sc) {
		System.out.println("\n===== FLIPFIT LOGIN =====");
		System.out.print("Username: ");
		String username = sc.next();
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
			System.out.println("\n✓ Logged in as Gym Owner");
			// Resolve or create owner by username
			com.flipfit.dao.OwnerDAO ownerDAO = com.flipfit.dao.OwnerDAO.getInstance();
			com.flipfit.bean.FlipFitGymOwner owner = ownerDAO.getOrCreateOwnerByName(username);
			GymOwnerMenu gymOwnerMenu = new GymOwnerMenu();
			gymOwnerMenu.showMenu(sc, owner.getOwnerId());
			break;
		case 2:
			System.out.println("\n✓ Logged in as Gym Customer");
			// Resolve or create customer by username
			com.flipfit.dao.CustomerDAO customerDAO = com.flipfit.dao.CustomerDAO.getInstance();
			com.flipfit.bean.FlipFitCustomer customer = customerDAO.getOrCreateCustomerByName(username);
			CustomerMenu customerMenu = new CustomerMenu();
			customerMenu.showMenu(sc, customer.getUserId());
			break;
		case 3:
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
			System.out.println("\n--- Redirecting to login ---");
			return 1; // Return 1 to continue (login again)
		case 2:
			System.out.println("\n--- Exiting application ---");
			return 0; // Return 0 to exit
		default:
			System.out.println("Invalid choice! Please try again.");
			return showLogoutMenu(sc);
		}
	}

}
