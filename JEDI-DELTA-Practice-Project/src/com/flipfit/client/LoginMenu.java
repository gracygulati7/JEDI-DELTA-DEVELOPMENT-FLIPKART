package com.flipfit.client;
import java.util.Scanner;

public class LoginMenu {
	public void login() {
		Scanner sc=new Scanner(System.in);
		System.out.println("Welcome to the FlipFit Application for Gym");
		System.out.print("Username: ");
		String username=sc.next();
		System.out.print("Password: ");
		String password=sc.next();
		System.out.print("Select Role: ");
		System.out.print("1. Gym Owner");
		System.out.print("2. Gym Customer ");
		System.out.print("3. Gym Admin ");
		int roleChoice=sc.nextInt();
		switch(roleChoice) {
		case 1: 
			GymOwnerMenu gymOwnerMenu=new GymOwnerMenu();
			gymOwnerMenu.showMenu();
			break;
		case 2: 
			CustomerMenu customerMenu=new CustomerMenu();
			customerMenu.showMenu();
			break;
		case 3: 
			AdminMenu adminMenu=new AdminMenu();
			adminMenu.showMenu();
			break;
		default:
			System.out.println("Invalid ROle selected");
		
		}
	}

}
