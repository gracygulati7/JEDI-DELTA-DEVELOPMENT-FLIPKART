package com.flipfit.client;
import com.flipfit.bean.TimeFrame;
import java.util.Scanner;
import com.flipfit.dao.GymCentreDAO;
import com.flipfit.dao.SlotDAO;

/**
 * 
 */
public class FlipFlitApplication {

    static {
        // Add dummy data
        GymCentreDAO gymCentreDAO = GymCentreDAO.getInstance();
        gymCentreDAO.addGymCentre(new com.flipfit.bean.FlipFitGymCenter(1, "Cult-fit", "Bangalore", "Karnataka", 560001, 100));
        gymCentreDAO.addGymCentre(new com.flipfit.bean.FlipFitGymCenter(2, "Anytime Fitness", "Delhi", "Delhi", 110001, 200));

        SlotDAO slotDAO = SlotDAO.getInstance();
        slotDAO.addSlot(new com.flipfit.bean.Slot(1, 1, java.time.LocalDate.now(), "6:00","7:30", 10));
        slotDAO.addSlot(new com.flipfit.bean.Slot(2, 1, java.time.LocalDate.now(),"5:00","7:30", 10));
        slotDAO.addSlot(new com.flipfit.bean.Slot(3, 2, java.time.LocalDate.now(), "8:30","9:30", 5));
    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		boolean continueApp = true;
		
		try (Scanner sc = new Scanner(System.in)) {
			while (continueApp) {
				LoginMenu loginMenu = new LoginMenu();
				int result = loginMenu.login(sc);
				
				if (result == 0) {
					// User chose to exit
					continueApp = false;
				}
				// If result == 1, loop continues and user can login again
			}
		}
		
		System.out.println("\n✓ Thank you for using FlipFit! Goodbye!");
	}

}
