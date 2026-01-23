/**
 * 
 */
package com.flipfit.client;

import java.util.Scanner;

/**
 * 
 */
public class CustomerMenu {
	public void showMenu() {

	Scanner sc=new Scanner(System.in);
	int choice;
	do {
		System.out.println("\nCustomer Menu");
		System.out.println("1. View Centres");
		System.out.println("2. View Booked Slots");
		System.out.println("3. Book Slot");
		System.out.println("4. Cancel Booking");
		System.out.println("5. Exit");
		choice= sc.nextInt();
		switch(choice) {
		case 1:
			System.out.println("Viewing centres... ");
			break;
		case 2:
			System.out.println("Viewing booked slots... ");
			break;
		case 3:
			System.out.println("Booking slot... ");
			break;
		case 4:
			System.out.println("Cancelling Booking... ");
			break;
		case 5:
			System.out.println("Exiting Customer Menu... ");
			break;
		default:
			System.out.println("Invalid option");
		}
	}while(choice!=5);
	/**
	 * @param args
	 */
	}	

}
