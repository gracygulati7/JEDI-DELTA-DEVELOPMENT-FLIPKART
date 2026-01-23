/**
 * 
 */
package com.flipfit.client;

import java.util.Scanner;

/**
 * 
 */
public class AdminMenu {
	public void showMenu() {
		Scanner sc=new Scanner(System.in);
		int choice;
		do {
			System.out.println("\nGym Admin Menu");
			System.out.println("1. Validate Owner");
			System.out.println("2. View Customers");
			System.out.println("3. Delete Owner");
			
			System.out.println("4. Exit");
			choice= sc.nextInt();
			switch(choice) {
			case 1:
				System.out.println("Adding centre... ");
				break;
			case 2:
				System.out.println("Viewing centres... ");
				break;
			case 3:
				System.out.println("Deleting Owner... ");
				break;
			
			case 4:
				System.out.println("Exiting Admin Menu... ");
				break;
			default:
				System.out.println("Invalid option");
			}
		}while(choice!=4);
		}
}
