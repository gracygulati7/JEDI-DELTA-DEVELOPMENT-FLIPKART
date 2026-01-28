package com.flipfit.business;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import com.flipfit.bean.Slot;
import com.flipfit.dao.CustomerDAO;
import com.flipfit.dao.GymCentreDAO;
import com.flipfit.dao.SlotDAO;
import com.flipfit.exceptions.DbConnectionException;
import com.flipfit.exceptions.UserNotFoundException;
import com.flipfit.bean.FlipFitCustomer;

public class UserServiceImpl implements UserService {

    private final SlotDAO slotDAO = SlotDAO.getInstance();
    private final CustomerDAO customerDAO = CustomerDAO.getInstance();
    private final GymCentreDAO gymCentreDAO = GymCentreDAO.getInstance();

    @Override
    public List<Slot> findAvailableSlots(int centreId) throws DbConnectionException {
        List<Slot> allSlots = slotDAO.getSlotsByCenterId(centreId);
        List<Slot> availableSlots = new ArrayList<>();

        for (Slot slot : allSlots) {
            if (slot.getDate() != null &&
                slot.getDate().isAfter(java.time.LocalDate.now().minusDays(1)) && // today or future
                !slot.isExpired() &&
                slot.getSeatsAvailable() > 0) {
                availableSlots.add(slot);
            }
        }

        // sort by date and start time
        availableSlots.sort((s1, s2) -> {
            int cmp = s1.getDate().compareTo(s2.getDate());
            if (cmp == 0) {
                return s1.getStartTime().compareTo(s2.getStartTime());
            }
            return cmp;
        });

        return availableSlots;
    }

    @Override
    public void viewProfile(int userId) throws DbConnectionException, UserNotFoundException {
        // DAO throws UserNotFoundException if missing, so no need for if (user != null)
        FlipFitCustomer user = customerDAO.getCustomerById(userId); 
        
        System.out.println("User ID: " + user.getUserId());
        System.out.println("Name: " + user.getFullName());
        System.out.println("Role: " + user.getRole());
    }

    @Override
    public void editProfile(int userId) throws DbConnectionException, UserNotFoundException {
        // DAO throws exception if not found
    	FlipFitCustomer user = customerDAO.getCustomerById(userId);

        Scanner sc = new Scanner(System.in);

        System.out.println("Editing profile for " + user.getFullName());
        System.out.println("Press ENTER to skip a field.");

        System.out.print("New Full Name (" + user.getFullName() + "): ");
        String newName = sc.nextLine();
        if (!newName.trim().isEmpty()) {
            user.setFullName(newName.trim());
        }

        System.out.print("New Email (" + user.getEmail() + "): ");
        String newEmail = sc.nextLine();
        if (!newEmail.trim().isEmpty()) {
            user.setEmail(newEmail.trim());
        }

        System.out.print("New Phone Number (" + user.getPhone() + "): ");
        String newPhone = sc.nextLine();
        if (!newPhone.trim().isEmpty()) {
        	try {
        		long phoneNum = Long.parseLong(newPhone.trim());
        		user.setPhone(phoneNum);
        	}
        	catch(NumberFormatException e) {
        		System.out.println("Invalid phone number. Skipping update for phone.");
        	}
        }

        customerDAO.updateCustomer(user); // persists changes, throws DbConnectionException on error
        System.out.println("✅ Profile updated successfully!");
    }
}