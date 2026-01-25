package com.flipfit.business;
package com.flipfit.repository.*;

import com.flipfit.bean.FlipFitGymCenter;
import com.flipfit.bean.FlipFitUser;
import com.flipfit.bean.Slot;
import com.flipfit.repository.FlipFitBookingRepository;
import com.flipfit.repository.FlipFitGymCenterRepository;
import com.flipfit.repository.FlipFitUserRepository;

import java.util.ArrayList;
import java.util.List;

public class CustomerServiceImpl implements CustomerService {
    FlipFitUserRepository userRepo = new FlipFitUserRepository();

    @Override
    public void editDetails(int userId, int pincode, String city) {
        FlipFitUser user = userRepo.getUserById(userId);
        if (user != null) {
            user.setPincode(pincode);
            user.setCity(city);
            userRepo.updateUser(user);

            System.out.println("Details updated successfully for User ID: " + userId);
        } else {
            System.out.println("User not found with ID: " + userId);
        }
    }

    FlipFitBookingRepository bookingRepo = new FlipFitBookingRepository();

    @Override
    public void viewBookedSlots(int userId) {
        System.out.println("\nBooked Slots for User ID: " + userId);
        List<Slot> bookedSlots = bookingRepo.getBookedSlotsByUserId(userId);
        if (bookedSlots.isEmpty()) {
            System.out.println("No bookings found.");
        } else {
            for (Slot slot : bookedSlots) {
                System.out.println("Slot ID: " + slot.getSlotId() +
                        " | Center ID: " + slot.getCenterId() +
                        " | Date: " + slot.getDate() +
                        " | Time: " + slot.getStartTime() + ":00 hrs");
            }
        }
    }

    @Override
    public boolean makePayment(int userId, int amount) {
        System.out.println("Payment of " + amount + " successful for user " + userId);
        return true;  // --------> implementaiton required
    }

    @Override
    public List<Object> viewCentres(int pincode, String city) {
        List<Object> visibleCenters = new ArrayList<>();
        List<FlipFitGymCenter> centersInPincode = centerRepo.getCentersByPincode(pincode);
        for (FlipFitGymCenter center : centersInPincode) {
            if (center.getCity().equalsIgnoreCase(city)) {
                String displayInfo = "City: " + center.getCity() +
                        ", Pincode: " + center.getPincode() +
                        ", State: " + center.getState();
                visibleCenters.add(displayInfo);
            }
        }
        return visibleCenters;
    }
}