package com.flipfit.client;

import com.flipfit.bean.Booking;
import com.flipfit.bean.FlipFitGymCenter;
import com.flipfit.bean.Slot;
import com.flipfit.business.BookingService;
import com.flipfit.business.BookingServiceImpl;
import com.flipfit.business.GymCentreService;
import com.flipfit.business.GymCentreServiceImpl;
import com.flipfit.business.NotificationServiceImpl;
import com.flipfit.business.UserService;
import com.flipfit.business.UserServiceImpl;
import com.flipfit.dao.GymCentreDAO;
import com.flipfit.business.CustomerService;
import com.flipfit.business.CustomerServiceImpl;
import com.flipfit.exceptions.*;
import com.flipfit.helper.InputValidator;

import java.util.List;
import java.util.Scanner;

public class CustomerMenu {
	private final BookingService bookingService = new BookingServiceImpl();
	private final GymCentreService gymCentreService = new GymCentreServiceImpl();
	private final UserService userService = new UserServiceImpl();
	private final GymCentreDAO gymCentreDAO = GymCentreDAO.getInstance();
	private final NotificationServiceImpl notificationService = NotificationServiceImpl.getInstance();
	private final CustomerService customerService = new CustomerServiceImpl();

	public void showMenu(Scanner sc, int userId) {
		int choice;
		do {
			System.out.println("\n===== CUSTOMER MENU =====");
			System.out.println("1. View Gyms");
			System.out.println("2. View My Bookings");
			System.out.println("3. Book a Slot");
			System.out.println("4. Cancel Booking");
			System.out.println("5. View Notifications");
			System.out.println("6. View Available Slots"); 
			System.out.println("7. View Profile");
			System.out.println("8. Edit Profile");
			System.out.println("9. View Payment Info");
			System.out.println("0. Logout");
			System.out.print("Enter your choice: ");
			choice = InputValidator.readInt(sc);
			
			try {
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
				case 5:
					viewNotifications(userId);
					break;
				case 6:
					viewAvailableSlotsUserService();
					break;
				case 7:
					userService.viewProfile(userId);
					break;
				case 8:
					userService.editProfile(userId);
					break;
				case 9:
					customerService.viewPaymentInfo(userId);
					break;
				case 0:
					System.out.println("Logging out from Customer Menu...");
					break;
				default:
					System.out.println("Invalid option");
				}
			} catch (DbConnectionException e) {
				System.out.println("System Error: " + e.getMessage());
			} catch (UserNotFoundException e) {
				System.out.println("User Error: " + e.getMessage());
			} catch (BookingFailedException e) {
				System.out.println("Booking Failed: " + e.getMessage());
			} catch (Exception e) {
				System.out.println("An unexpected error occurred: " + e.getMessage());
			}
		} while (choice != 0);
	}

	private void viewAvailableSlotsUserService() throws DbConnectionException {
		System.out.println("\n===== VIEW ALL AVAILABLE SLOTS (USER SERVICE) =====");
		List<FlipFitGymCenter> gyms = gymCentreDAO.getAllCentres();
		if (gyms.isEmpty()) {
			System.out.println("No gyms available.");
			return;
		}
		for (FlipFitGymCenter gym : gyms) {
			List<Slot> slots = userService.findAvailableSlots(gym.getGymId());
			System.out.println("Gym: " + gym.getGymName() + " (ID: " + gym.getGymId() + ")");
			if (slots.isEmpty()) {
				System.out.println("  └─ No available slots");
			} else {
				for (Slot slot : slots) {
					System.out.println("  Slot ID: " + slot.getSlotId() + " | Date: " + slot.getDate() + " | Time: "
							+ slot.getStartTime() + "-" + slot.getEndTime() + " | Seats: " + slot.getSeatsAvailable()
							+ "/" + slot.getTotalSeats());
				}
			}
		}
	}

	private void viewGyms() throws DbConnectionException {
		System.out.println("\nAvailable Gyms:");
		List<FlipFitGymCenter> gyms = gymCentreDAO.getAllCentres();
		for (FlipFitGymCenter gym : gyms) {
			System.out.println(
					"ID: " + gym.getGymId() + ", Name: " + gym.getGymName() + ", Location: " + gym.getLocation());
			List<Slot> slots = gymCentreService.getSlotsByCentreId(gym.getGymId());
			for (Slot slot : slots) {
				String dateStr = (slot.getDate() != null) ? slot.getDate().toString() : "N/A";
				System.out.println("  " + slot + " | Date: " + dateStr);
			}
		}
	}

	private void viewMyBookings(int userId) throws DbConnectionException {
		System.out.println("\n===== MY BOOKINGS =====");
		List<Booking> bookings = bookingService.getBookingsByUserId(userId); 

		if (bookings.isEmpty()) {
			System.out.println("No active bookings found.");
		} else {
			com.flipfit.dao.SlotDAO slotDAO = com.flipfit.dao.SlotDAO.getInstance();
			com.flipfit.dao.GymCentreDAO gymDAO = com.flipfit.dao.GymCentreDAO.getInstance();

			for (Booking booking : bookings) {
				try {
    				Slot slot = slotDAO.getSlotById(booking.getUserId(), booking.getSlotId(), booking.getCenterId());
    				if (slot != null) {
    				    try {
        					String gymName = gymDAO.getGymCentreById(booking.getCenterId()).getGymName();
        					System.out.println("Booking #" + booking.getBookingId() + " | Gym: " + gymName + " | Date: "
        							+ booking.getSlotDate() + " | Time: " + booking.getStartTime());
    				    } catch (CentreNotFoundException e) {
    				        System.out.println("Booking #" + booking.getBookingId() + " (Center details unavailable)");
    				    }
    				}
				} catch (Exception e) {
				    // Prevent loop break on single bad record
				    System.err.println("Error displaying booking " + booking.getBookingId());
				}
			}
		}
	}

	private void bookSlot(Scanner sc, int userId) throws DbConnectionException, BookingFailedException, UserNotFoundException {
		System.out.println("\n===== AVAILABLE GYM CENTERS =====");
		List<FlipFitGymCenter> gyms = gymCentreDAO.getAllCentres();

		if (gyms.isEmpty()) {
			System.out.println("No gym centers available.");
			return;
		}

		for (FlipFitGymCenter gym : gyms) {
			System.out.println("Center ID: " + gym.getGymId() + " | Name: " + gym.getGymName() + " | Location: "
					+ gym.getLocation());
		}

		System.out.print("\nEnter Center ID: ");
		int centerId = InputValidator.readInt(sc);

        FlipFitGymCenter selectedCenter;
        try {
		    selectedCenter = gymCentreDAO.getGymCentreById(centerId);
        } catch (CentreNotFoundException e) {
			System.out.println("❌ " + e.getMessage());
			return;
        }

		System.out.println("✓ Selected Center: " + selectedCenter.getGymName());

		System.out.print("\nEnter booking date (YYYY-MM-DD): ");
		String dateStr = sc.next();
		java.time.LocalDate bookingDate;
		try {
			bookingDate = java.time.LocalDate.parse(dateStr);
		} catch (java.time.format.DateTimeParseException e) {
			System.out.println("❌ Error: Invalid date format!");
			return;
		}

		// Simplified display logic calling DAO directly for listing
		com.flipfit.dao.SlotDAO slotDAO = com.flipfit.dao.SlotDAO.getInstance();
		List<Slot> allSlots = slotDAO.getAllSlots();
		
		boolean found = false;
        for(Slot s : allSlots) {
             if(s.getCenterId() == centerId && s.getDate() != null && s.getDate().equals(bookingDate)) {
                 System.out.println("Slot ID: " + s.getSlotId() + " | Time: " + s.getStartTime() + " - " + s.getEndTime() + " | Seats: " + s.getSeatsAvailable() + "/" + s.getTotalSeats());
                 found = true;
             }
        }
        
        if(!found) {
            System.out.println("No slots found for this date.");
            return;
        }

		System.out.print("\nEnter Slot ID to book: ");
		int slotId = InputValidator.readInt(sc);
		
		System.out.print("\nEnter payment amount: ");
		int amount = InputValidator.readInt(sc);

		boolean paymentSuccess = customerService.makePayment(userId, amount);

		if (!paymentSuccess) {
			System.out.println("❌ Payment failed. Booking cancelled.");
			return;
		}

		// This will now throw BookingFailedException if validation fails
		Booking booking = bookingService.createBooking(userId, slotId, centerId);
		
		System.out.println("\n✓ BOOKING CONFIRMED! ID: " + booking.getBookingId());
	}

	private void cancelBooking(Scanner sc, int userId) throws DbConnectionException, BookingFailedException {
		System.out.print("Enter Booking ID to cancel: ");
		int bookingId = InputValidator.readInt(sc);
        
        // Logic handled in service/DAO, exceptions will bubble up if ID is invalid or DB fails
		bookingService.cancelBooking(bookingId);
		System.out.println("✓ Booking cancelled successfully.");
	}

	private void viewNotifications(int userId) {
		System.out.println("\n╔════════════════════════════════════════╗");
		System.out.println("║        YOUR NOTIFICATIONS              ║");
		System.out.println("╚════════════════════════════════════════╝");
		notificationService.printUserNotifications(userId);
	}
}