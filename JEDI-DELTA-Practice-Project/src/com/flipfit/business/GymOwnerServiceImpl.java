package com.flipfit.business;
import com.flipfit.bean.FlipFitGymCenter;
import com.flipfit.bean.Slot;
import com.flipfit.repository.FlipFitGymCenterRepository;
import java.util.ArrayList;
import java.util.List;

public class GymOwnerServiceImpl implements GymOwnerService {
    @Override
    FlipFitGymCenterRepository centerRepo = new FlipFitGymCenterRepository();

    public void addCentre(int ownerId) {
        System.out.println("Adding a new center for Owner ID: " + ownerId);
        int newCenterId = (int)(Math.random() * 1000);
        String city = "Bangalore";
        String state = "Karnataka";
        int pincode = 560001;
        int capacity = 50;
        FlipFitGymCenter newCenter = new FlipFitGymCenter(newCenterId, city, state, pincode, capacity);
        centerRepo.addCenter(newCenter);
        System.out.println("Successfully added Center " + newCenterId + " in " + city);
    }

    @Override
    public void viewCentres(int ownerId) {
        System.out.println("\n--- Listing Centers for Owner ID: " + ownerId + " ---");
        List<FlipFitGymCenter> myCenters = centerRepo.getCentersByOwnerId(ownerId);
        if (myCenters.isEmpty()) {
            System.out.println("You have not registered any centers yet.");
        } else {
            for (FlipFitGymCenter center : myCenters) {
                System.out.println("[Center ID: " + center.getCenterId() +
                        "] Location: " + center.getCity() +
                        ", " + center.getPincode() +
                        " | Capacity: " + center.getCapacity());
            }
        }
        System.out.println("------------------------------------------");
    }

    @Override
    public void viewCustomers(int centreId) {
        System.out.println("Listing customers for centre ID: " + centreId);
    }

//    @Overrideride
//    public void viewPayments(int ownerId) {
//        System.out.println("Displaying payment history...");
//    }

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

    @Override
    public List<Slot> getBookedSlots(int centreId) {
        List<Slot> bookedSlots = new ArrayList<>();
        FlipFitGymCenter center = centerRepo.getCenterById(centreId);

        if (center != null) {
            for (Slot slot : center.getSlots()) {
                if (slot.isBooked()) {
                    bookedSlots.add(slot);
                }
            }
        }
        return bookedSlots;
    }

    @Override
    public List<Slot> getAvailableSlots(int centreId) {
        List<Slot> availableSlots = new ArrayList<>();
        FlipFitGymCenter center = centerRepo.getCenterById(centreId);

        if (center != null) {
            for (Slot slot : center.getSlots()) {
                if (!slot.isBooked()) {
                    availableSlots.add(slot);
                }
            }
        }
        return availableSlots;
    }
}