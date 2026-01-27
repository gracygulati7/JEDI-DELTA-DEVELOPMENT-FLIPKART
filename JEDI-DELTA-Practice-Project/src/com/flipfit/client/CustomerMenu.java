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

import java.util.List;
import java.util.Scanner;

import com.flipfit.helper.InputValidator;

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
			System.out.println("6. View Available Slots"); // via userService
			System.out.println("7. View Profile");
			System.out.println("8. Edit Profile");
			System.out.println("9. View Payment Info");
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
		} while (choice != 0);
	}

	private void viewAvailableSlotsUserService() {
		System.out.println("\n===== VIEW ALL AVAILABLE SLOTS (USER SERVICE) =====");
		List<FlipFitGymCenter> gyms = gymCentreDAO.getGymCentres();
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

	private void viewGyms() {
		System.out.println("\nAvailable Gyms:");
		List<FlipFitGymCenter> gyms = gymCentreDAO.getGymCentres();
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

	private void viewMyBookings(int userId) {
		System.out.println("\n===== MY BOOKINGS =====");
		List<Booking> bookings = bookingService.getBookingsByUserId(userId); // This is now filtered by the service

		if (bookings.isEmpty()) {
			System.out.println("No active bookings found.");
		} else {
			com.flipfit.dao.SlotDAO slotDAO = com.flipfit.dao.SlotDAO.getInstance();
			com.flipfit.dao.GymCentreDAO gymDAO = com.flipfit.dao.GymCentreDAO.getInstance();

			for (Booking booking : bookings) {
				Slot slot = slotDAO.getSlotById(booking.getUserId(), booking.getSlotId(), booking.getCenterId());
				if (slot != null) {
					String gymName = gymDAO.getGymCentreById(booking.getCenterId()).getGymName();
					System.out.println("Booking #" + booking.getBookingId() + " | Gym: " + gymName + " | Date: "
							+ booking.getSlotDate() + " | Time: " + booking.getStartTime());
				}
			}
		}
	}

	private void bookSlot(Scanner sc, int userId) {
		// Step 1: Display available centers
		System.out.println("\n===== AVAILABLE GYM CENTERS =====");
		List<FlipFitGymCenter> gyms = gymCentreDAO.getGymCentres();

		if (gyms.isEmpty()) {
			System.out.println("No gym centers available.");
			return;
		}

		for (FlipFitGymCenter gym : gyms) {
			System.out.println("Center ID: " + gym.getGymId() + " | Name: " + gym.getGymName() + " | Location: "
					+ gym.getLocation());
		}

		// Step 2: Ask for center ID
		System.out.print("\nEnter Center ID: ");
		int centerId = InputValidator.readInt(sc);

		FlipFitGymCenter selectedCenter = gymCentreDAO.getGymCentreById(centerId);
		if (selectedCenter == null) {
			System.out.println("❌ Invalid Center ID!");
			return;
		}

		System.out.println("✓ Selected Center: " + selectedCenter.getGymName());

		// Step 3: Ask for booking date
		System.out.print("\nEnter booking date (YYYY-MM-DD format, e.g., 2026-01-25): ");
		String dateStr = sc.next();
		java.time.LocalDate bookingDate;
		try {
			bookingDate = java.time.LocalDate.parse(dateStr);
		} catch (java.time.format.DateTimeParseException e) {
			System.out.println("❌ Error: Invalid date format! Please use YYYY-MM-DD.");
			return;
		}

		// Step 4: Display available slots for this center and date
		System.out.println("\n===== SLOTS FOR CENTER " + centerId + " (" + selectedCenter.getGymName() + ") ON "
				+ bookingDate + " =====");
		com.flipfit.dao.SlotDAO slotDAO = com.flipfit.dao.SlotDAO.getInstance();

		List<com.flipfit.bean.Slot> allSlots = slotDAO.getAllSlots();
		java.util.List<com.flipfit.bean.Slot> slotsForCenterAndDate = new java.util.ArrayList<>();
		java.util.List<com.flipfit.bean.Slot> fullSlotsForCenterAndDate = new java.util.ArrayList<>();

		for (com.flipfit.bean.Slot slot : allSlots) {
			if (slot.getDate() != null && slot.getDate().equals(bookingDate) && slot.getCenterId() == centerId
					&& !slot.isExpired()) {

				if (slot.getSeatsAvailable() > 0) {
					slotsForCenterAndDate.add(slot);
				} else {
					fullSlotsForCenterAndDate.add(slot);
				}
			}
		}

		// Display available slots
		if (slotsForCenterAndDate.isEmpty() && fullSlotsForCenterAndDate.isEmpty()) {
			System.out.println("❌ No slots available for this center on the selected date.");
			return;
		}

		if (!slotsForCenterAndDate.isEmpty()) {
			System.out.println("\n✓ AVAILABLE SLOTS:");
			for (com.flipfit.bean.Slot slot : slotsForCenterAndDate) {
				System.out.println("  Slot ID: " + slot.getSlotId() + " | Center ID: " + slot.getCenterId()
						+ " | Time: " + slot.getStartTime() + " - " + slot.getEndTime() + " | Available Seats: "
						+ slot.getSeatsAvailable() + "/" + slot.getTotalSeats());
			}
		} else {
			System.out.println("\n❌ No available slots (all slots are full or expired for this date)");
		}

		// Display full slots with waitlist option
		if (!fullSlotsForCenterAndDate.isEmpty()) {
			System.out.println("\n⏳ FULL SLOTS (Waitlist Available):");
			for (com.flipfit.bean.Slot slot : fullSlotsForCenterAndDate) {
				System.out.println("  Slot ID: " + slot.getSlotId() + " | Center ID: " + slot.getCenterId()
						+ " | Time: " + slot.getStartTime() + " - " + slot.getEndTime() + " | Status: FULL (0/"
						+ slot.getTotalSeats() + ")");
			}
		}

		if (slotsForCenterAndDate.isEmpty()) {
			System.out.println("\n💡 You can still join the waitlist for full slots!");
		}

		// Step 5: Ask for slot selection
		System.out.print("\nEnter Slot ID to book: ");
		int slotId = InputValidator.readInt(sc);

		// Validate slot selection
		com.flipfit.bean.Slot selectedSlot = null;
		boolean isFullSlot = false;

		for (com.flipfit.bean.Slot slot : slotsForCenterAndDate) {
			if (slot.getSlotId() == slotId) {
				selectedSlot = slot;
				break;
			}
		}

		if (selectedSlot == null) {
			for (com.flipfit.bean.Slot slot : fullSlotsForCenterAndDate) {
				if (slot.getSlotId() == slotId) {
					selectedSlot = slot;
					isFullSlot = true;
					break;
				}
			}
		}

		if (selectedSlot == null) {
			System.out.println("❌ Invalid Slot ID!");
			return;
		}

		if (!isFullSlot) {
			System.out.println("\n💰 Payment required to confirm this booking.");
			System.out.print("Enter payment amount: ");
			int amount = InputValidator.readInt(sc);

			boolean paymentSuccess = customerService.makePayment(userId, amount);

			if (!paymentSuccess) {
				System.out.println("❌ Payment failed. Booking cancelled.");
				return;
			}
		} else {
			System.out.println(
					"\nℹ️ You are joining the waitlist. Payment will be required when a seat becomes available.");
		}

		// Step 6: Create booking with centerId to ensure correct slot is retrieved
		Booking booking = bookingService.createBooking(userId, slotId, centerId);
		if (booking != null) {
			if (isFullSlot) {
				// Slot was full, customer added to waitlist
				System.out.println("\n📋 ADDED TO WAITLIST");
				System.out.println("  Center: " + selectedCenter.getGymName() + " (ID: " + centerId + ")");
				System.out.println("  Date: " + bookingDate);
				System.out.println("  Time: " + selectedSlot.getStartTime() + " - " + selectedSlot.getEndTime());
				System.out.println("  Status: Slot is full. You're in the waitlist queue.");
				System.out.println("  ℹ️  You'll be notified when a seat becomes available.");
			} else {
				// Slot had available seats, booking confirmed
				// selectedSlot.setSeatsAvailable(selectedSlot.getSeatsAvailable() - 1);
				System.out.println("\n✓ BOOKING CONFIRMED");
				System.out.println("  Booking ID: " + booking.getBookingId());
				System.out.println("  Center: " + selectedCenter.getGymName() + " (ID: " + centerId + ")");
				System.out.println("  Date: " + bookingDate);
				System.out.println("  Time: " + selectedSlot.getStartTime() + " - " + selectedSlot.getEndTime());
				System.out.println(
						"  Remaining Seats: " + selectedSlot.getSeatsAvailable() + "/" + selectedSlot.getTotalSeats());
			}
		} else {
			System.out.println("❌ Booking failed. Time conflict or invalid slot.");
		}
	}

	private void cancelBooking(Scanner sc, int userId) {
		System.out.print("Enter Booking ID to cancel: ");
		int bookingId = InputValidator.readInt(sc);

		// Check if booking exists and belongs to user before attempting cancellation
		List<Booking> myBookings = bookingService.getBookingsByUserId(userId);
		boolean exists = myBookings.stream().anyMatch(b -> b.getBookingId() == bookingId);

		if (exists) {
			bookingService.cancelBooking(bookingId);
			System.out.println("✓ Booking cancelled successfully.");
		} else {
			System.out.println("❌ Error: Booking ID not found in your active bookings.");
		}
	}

	private void viewNotifications(int userId) {
		System.out.println("\n╔════════════════════════════════════════╗");
		System.out.println("║        YOUR NOTIFICATIONS              ║");
		System.out.println("╚════════════════════════════════════════╝");

		notificationService.printUserNotifications(userId);
	}
}
