package com.flipfit.client;

import java.util.Scanner;
import com.flipfit.dao.GymCentreDAO;
import com.flipfit.dao.OwnerDAO;
import com.flipfit.dao.SlotDAO;
import com.flipfit.dao.UserDAO;
import com.flipfit.exceptions.CentreNotFoundException;
import com.flipfit.exceptions.DbConnectionException;
import com.flipfit.exceptions.UserNotFoundException;

public class FlipFlitApplication {

    static {
        try {
            UserDAO userDAO = UserDAO.getInstance();
            OwnerDAO ownerDAO = OwnerDAO.getInstance();
            GymCentreDAO gymCentreDAO = GymCentreDAO.getInstance();
            SlotDAO slotDAO = SlotDAO.getInstance();

            // 1. Ensure the Owner exists
            try {
                ownerDAO.getOwnerById(1);
            } catch (UserNotFoundException e) {
                userDAO.registerUser(1, "Admin Owner", "admin@flipfit.com", "admin123", "OWNER");
                ownerDAO.addOwnerDetails(1, "ABCDE1234F", "123456789012", "GSTIN123");
                System.out.println("✓ Dummy Owner created.");
            }

            // 2. Add Gym Centres
            if (!gymCentreDAO.centreIdExists(1)) {
                com.flipfit.bean.FlipFitGymCenter gym1 = new com.flipfit.bean.FlipFitGymCenter(1, "Cult-fit", "Bangalore", "Karnataka", 560001, 100);
                gym1.setOwnerId(1); 
                gymCentreDAO.addGymCentre(gym1);

                com.flipfit.bean.FlipFitGymCenter gym2 = new com.flipfit.bean.FlipFitGymCenter(2, "Anytime Fitness", "Delhi", "Delhi", 110001, 200);
                gym2.setOwnerId(1); 
                gymCentreDAO.addGymCentre(gym2);
                
                System.out.println("✓ Dummy Gym Centres added.");
            }

            // 3. Add Slots
            if (slotDAO.getSlotById(1) == null) {
                slotDAO.addSlot(new com.flipfit.bean.Slot(1, 1, java.time.LocalDate.now(), "06:00:00", "07:30:00", 10));
            }
            if (slotDAO.getSlotById(2) == null) {
                slotDAO.addSlot(new com.flipfit.bean.Slot(2, 1, java.time.LocalDate.now(), "05:00:00", "07:30:00", 10));
            }

        } catch (DbConnectionException e) {
            System.err.println("CRITICAL: Database connection failed during startup. " + e.getMessage());
        } catch (Exception e) {
            System.err.println("System initialization warning: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        boolean continueApp = true;
        
        try (Scanner sc = new Scanner(System.in)) {
            while (continueApp) {
                LoginMenu loginMenu = new LoginMenu();
                int result = loginMenu.showStartMenu(sc);
                
                if (result == 0) {
                    continueApp = false;
                }
            }
        }
        
        System.out.println("\n✓ Thank you for using FlipFit! Goodbye!");
    }
}