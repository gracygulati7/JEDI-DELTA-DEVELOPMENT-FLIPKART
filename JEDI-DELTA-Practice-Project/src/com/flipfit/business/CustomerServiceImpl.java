package com.flipfit.business;

import com.flipfit.dao.CustomerDAO;
import com.flipfit.bean.FlipFitCustomer;
import com.flipfit.exceptions.DbConnectionException;
import com.flipfit.exceptions.UserNotFoundException;
import java.util.Scanner;

public class CustomerServiceImpl implements CustomerService {
    private final CustomerDAO customerDAO = CustomerDAO.getInstance();

    @Override
    public boolean makePayment(int userId, int amount) throws DbConnectionException, UserNotFoundException {
        
        // DAO now throws UserNotFoundException if ID is missing, so we don't need to check == null
        FlipFitCustomer customer = customerDAO.getCustomerById(userId);

        Scanner sc = new Scanner(System.in);
        
        System.out.println("\n===== PAYMENT =====");
        System.out.println("Amount to pay: ₹" + amount);
        System.out.println("Select Payment Method:");
        System.out.println("1. Card");
        System.out.println("2. UPI");

        int choice = sc.nextInt();
        sc.nextLine(); // consume newline

        String paymentInfo;

        switch (choice) {
            case 1:
                System.out.print("Enter Card Number (last 4 digits): ");
                paymentInfo = sc.nextLine();
                // DAO throws DbConnectionException
                customerDAO.updatePaymentDetails(userId, 1, paymentInfo);
                break;

            case 2:
                System.out.print("Enter UPI ID: ");
                paymentInfo = sc.nextLine();
                // DAO throws DbConnectionException
                customerDAO.updatePaymentDetails(userId, 2, paymentInfo);
                break;

            default:
                System.out.println("❌ Invalid payment option.");
                return false;
        }

        System.out.println("✅ Payment of ₹" + amount + " successful!");
        return true;
    }

    @Override
    public void viewPaymentInfo(int userId) throws DbConnectionException, UserNotFoundException {
        
        // DAO now throws UserNotFoundException if ID is missing
        FlipFitCustomer customer = CustomerDAO.getInstance().getCustomerById(userId);

        System.out.println("\n===== PAYMENT DETAILS =====");

        if (customer.getPaymentType() == 0) {
            System.out.println("No payment method saved.");
            return;
        }

        String paymentMethod = (customer.getPaymentType() == 1) ? "Card" : "UPI";

        System.out.println("Payment Method: " + paymentMethod);

        if (customer.getPaymentType() == 1) {
            // Mask card number
            String info = customer.getPaymentInfo();
            System.out.println("Card: **** **** **** " + info);
        } else {
            System.out.println("UPI ID: " + customer.getPaymentInfo());
        }
    }
    
    @Override
    public void viewBookedSlots(int userId) {
        System.out.println("Displaying booked slots for User: " + userId);
    }

    @Override
    public boolean checkBookingConflicts(int userId, int slotId) {
        return false; 
    }

    @Override
    public java.util.List<Object> viewCentres(String city) {
        return null; 
    }
}