/**
 * 
 */
package com.flipfit.client;

import java.util.Scanner;

/**
 * 
 */
public class GymOwnerMenu {
	public void showMenu() {
		try (Scanner sc = new Scanner(System.in)) {
			int choice;
			do {
				System.out.println("\nGym Owner Menu");
				System.out.println("1. Add Centre");
				System.out.println("2. View Centres");
				System.out.println("3. View Customers");
				
				System.out.println("4. Exit");
				choice = sc.nextInt();
				switch (choice) {
				case 1:
					System.out.println("Adding centre... ");
					break;
				case 2:
					System.out.println("Viewing centres... ");
					break;
				case 3:
					System.out.println("Viewwing Customers... ");
					break;
				
				case 4:
					System.out.println("Exiting Gym Owner Menu... ");
					break;
				default:
					System.out.println("Invalid option");
				}
			} while (choice != 4);
		}
	}

}
