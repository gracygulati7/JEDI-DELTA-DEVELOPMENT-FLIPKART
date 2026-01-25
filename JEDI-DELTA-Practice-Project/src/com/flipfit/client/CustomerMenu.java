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

import com.flipfit.helper.InputValidator;

public class CustomerMenu {
    private final BookingService bookingService = new BookingServiceImpl();
    private final GymCentreService gymCentreService = new GymCentreServiceImpl();
    private final GymCentreDAO gymCentreDAO = GymCentreDAO.getInstance();

    public void showMenu(Scanner sc, int userId) {
        int choice;
        do {
            System.out.println("\n===== CUSTOMER MENU =====");
            System.out.println("1. View Gyms");
            System.out.println("2. View My Bookings");
            System.out.println("3. Book a Slot");
            System.out.println("4. Cancel Booking");
            System.out.println("0. Logout");
            System.out.print("Enter your choice: ");
            choice = InputValidator.readInt(sc);
            switch (choice) {
                case 1:
                    viewGyms();
                    break;
                case 2:
                    viewMyBookings(userId);
                    break;
                case 3:
                    bookSlot(sc, userId);
                    break;
                case 4:
                    cancelBooking(sc, userId);
                    break;
                case 0:
                    System.out.println("Logging out from Customer Menu...");
                    break;
                default:
                    System.out.println("Invalid option");
            }
        } while (choice != 0);
    }

    private void viewGyms() {
        System.out.println("\nAvailable Gyms:");
        List<FlipFitGymCenter> gyms = gymCentreDAO.getGymCentres();
        for (FlipFitGymCenter gym : gyms) {
            System.out.println("ID: " + gym.getGymId() + ", Name: " + gym.getGymName() + ", Location: " + gym.getLocation());
            List<Slot> slots = gymCentreService.getSlotsByCentreId(gym.getGymId());
            for (Slot slot : slots) {
                String dateStr = (slot.getDate() != null) ? slot.getDate().toString() : "N/A";
                System.out.println("  " + slot + " | Date: " + dateStr);
            }
        }
    }

    private void viewMyBookings(int userId) {
        System.out.println("\n===== MY BOOKINGS =====");
        List<Booking> bookings = bookingService.getBookingsByUserId(userId);
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
        } else {
            com.flipfit.dao.SlotDAO slotDAO = com.flipfit.dao.SlotDAO.getInstance();
            com.flipfit.dao.GymCentreDAO gymDAO = com.flipfit.dao.GymCentreDAO.getInstance();
            
            for (Booking booking : bookings) {
                com.flipfit.bean.Slot slot = slotDAO.getSlotById(booking.getSlotId());
                if (slot != null) {
                    com.flipfit.bean.FlipFitGymCenter gym = gymDAO.getGymCentreById(slot.getCenterId());
                    String gymName = (gym != null) ? gym.getGymName() : "Unknown Gym";
                    String dateStr = (slot.getDate() != null) ? slot.getDate().toString() : "N/A";
                    System.out.println("\nBooking #" + booking.getBookingId() + 
                        " | Gym: " + gymName + 
                        " | Date: " + dateStr +
                        " | Slot ID: " + slot.getSlotId() + 
                        " | Time: " + slot.getStartTime() + ":00hrs" +
                        " | Seats: " + slot.getSeatsAvailable());
                } else {
                    System.out.println("\nBooking #" + booking.getBookingId() + " | Slot #" + booking.getSlotId() + " (slot not found)");
                }
            }
        }
    }

    private void bookSlot(Scanner sc, int userId) {
        System.out.print("Enter booking date (YYYY-MM-DD format, e.g., 2026-01-25): ");
        String dateStr = sc.next();
        java.time.LocalDate bookingDate;
        try {
            bookingDate = java.time.LocalDate.parse(dateStr);
        } catch (java.time.format.DateTimeParseException e) {
            System.out.println("❌ Error: Invalid date format! Please use YYYY-MM-DD.");
            return;
        }
        
        // Show available slots for this date
        System.out.println("\n===== AVAILABLE SLOTS FOR " + bookingDate + " =====");
        com.flipfit.dao.SlotDAO slotDAO = com.flipfit.dao.SlotDAO.getInstance();
        com.flipfit.dao.GymCentreDAO gymDAO = com.flipfit.dao.GymCentreDAO.getInstance();
        
        List<com.flipfit.bean.Slot> allSlots = slotDAO.getAllSlots();
        java.util.List<com.flipfit.bean.Slot> availableSlotsForDate = new java.util.ArrayList<>();
        
        for (com.flipfit.bean.Slot slot : allSlots) {
            if (slot.getDate() != null && slot.getDate().equals(bookingDate) && slot.getSeatsAvailable() > 0) {
                availableSlotsForDate.add(slot);
            }
        }
        
        if (availableSlotsForDate.isEmpty()) {
            System.out.println("No slots available for this date.");
            return;
        }
        
        for (com.flipfit.bean.Slot slot : availableSlotsForDate) {
            com.flipfit.bean.FlipFitGymCenter gym = gymDAO.getGymCentreById(slot.getCenterId());
            String gymName = (gym != null) ? gym.getGymName() : "Unknown Gym";
            System.out.println("Slot ID: " + slot.getSlotId() + " | Gym: " + gymName + 
                " | Time: " + slot.getStartTime() + ":00hrs | Available Seats: " + slot.getSeatsAvailable());
        }
        
        System.out.print("\nEnter Slot ID to book: ");
        int slotId = InputValidator.readInt(sc);
        Booking booking = bookingService.createBooking(userId, slotId);
        if (booking != null) {
            System.out.println("✓ Booking successful! Booking ID: " + booking.getBookingId());
        } else {
            System.out.println("❌ Booking failed. Slot may be full, invalid, or not available for selected date.");
        }
    }

    private void cancelBooking(Scanner sc, int userId) {
        System.out.print("Enter Booking ID to cancel: ");
        int bookingId = InputValidator.readInt(sc);
        bookingService.cancelBooking(bookingId);
        System.out.println("Booking cancelled successfully.");
    }
}

