package com.flipfit.client;

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
        slotDAO.addSlot(new com.flipfit.bean.Slot(1, 1, 6, 10));
        slotDAO.addSlot(new com.flipfit.bean.Slot(2, 1, 7, 10));
        slotDAO.addSlot(new com.flipfit.bean.Slot(3, 2, 8, 5));
    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LoginMenu loginMenu=new LoginMenu();
		loginMenu.login();
	}

}
