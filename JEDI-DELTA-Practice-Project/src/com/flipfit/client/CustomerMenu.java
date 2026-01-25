package com.flipfit.client;

import com.flipfit.bean.Booking;
import com.flipfit.bean.FlipFitGymCenter;
import com.flipfit.bean.Slot;
import com.flipfit.business.BookingService;
import com.flipfit.business.BookingServiceImpl;
import com.flipfit.business.GymCentreService;
import com.flipfit.business.GymCentreServiceImpl;
import com.flipfit.dao.GymCentreDAO;

import java.util.List;
import java.util.Scanner;

public class CustomerMenu {
    private final BookingService bookingService = new BookingServiceImpl();
    private final GymCentreService gymCentreService = new GymCentreServiceImpl();
    private final GymCentreDAO gymCentreDAO = GymCentreDAO.getInstance();
    private final int userId = 1; // Hardcoded user id for now

    public void showMenu() {
        Scanner sc = new Scanner(System.in);
        int choice;
        do {
            System.out.println("\nCustomer Menu");
            System.out.println("1. View Gyms");
            System.out.println("2. View My Bookings");
            System.out.println("3. Book a Slot");
            System.out.println("4. Cancel Booking");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            switch (choice) {
                case 1:
                    viewGyms();
                    break;
                case 2:
                    viewMyBookings();
                    break;
                case 3:
                    bookSlot(sc);
                    break;
                case 4:
                    cancelBooking(sc);
                    break;
                case 5:
                    System.out.println("Exiting Customer Menu...");
                    break;
                default:
                    System.out.println("Invalid option");
            }
        } while (choice != 5);
    }

    private void viewGyms() {
        System.out.println("\nAvailable Gyms:");
        List<FlipFitGymCenter> gyms = gymCentreDAO.getGymCentres();
        for (FlipFitGymCenter gym : gyms) {
            System.out.println("ID: " + gym.getGymId() + ", Name: " + gym.getGymName() + ", Location: " + gym.getLocation());
            List<Slot> slots = gymCentreService.getSlotsByCentreId(gym.getGymId());
            for (Slot slot : slots) {
                System.out.println("  " + slot);
            }
        }
    }

    private void viewMyBookings() {
        System.out.println("\nMy Bookings:");
        List<Booking> bookings = bookingService.getBookingsByUserId(userId);
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
        } else {
            for (Booking booking : bookings) {
                System.out.println("Booking ID: " + booking.getBookingId() + ", Slot ID: " + booking.getSlotId());
            }
        }
    }

    private void bookSlot(Scanner sc) {
        System.out.print("Enter Slot ID to book: ");
        int slotId = sc.nextInt();
        Booking booking = bookingService.createBooking(userId, slotId);
        if (booking != null) {
            System.out.println("Booking successful! Booking ID: " + booking.getBookingId());
        } else {
            System.out.println("Booking failed. Slot may be full or invalid.");
        }
    }

    private void cancelBooking(Scanner sc) {
        System.out.print("Enter Booking ID to cancel: ");
        int bookingId = sc.nextInt();
        bookingService.cancelBooking(bookingId);
        System.out.println("Booking cancelled successfully.");
    }
}
