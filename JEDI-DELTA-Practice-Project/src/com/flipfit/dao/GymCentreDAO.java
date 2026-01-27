package com.flipfit.dao;

import com.flipfit.bean.FlipFitGymCenter;
import java.util.ArrayList;
import java.util.List;

public class GymCentreDAO {

    private static GymCentreDAO instance = null;
    private final List<FlipFitGymCenter> gymCentres = new ArrayList<>();
   private int nextCentreId = 101; // Auto-generated IDs start from 101

    private GymCentreDAO() {}

    public static GymCentreDAO getInstance() {
        if (instance == null) {
            instance = new GymCentreDAO();
        }
        return instance;
    }

    public void addGymCentre(FlipFitGymCenter gymCentre) {
        gymCentres.add(gymCentre);
    }

   /**
    * Get next auto-generated centre ID
    */
   public int getNextCentreId() {
       return nextCentreId++;
   }

   /**
    * Check if a centre with given ID already exists
    */
   public boolean centreIdExists(int centreId) {
       for (FlipFitGymCenter centre : gymCentres) {
           if (centre.getCenterId() == centreId) {
               return true;
           }
       }
       return false;
   }

    public List<FlipFitGymCenter> getGymCentres() {
        return gymCentres;
    }

    public FlipFitGymCenter getGymCentreById(int centreId) {
        for (FlipFitGymCenter centre : gymCentres) {
            if (centre.getCenterId() == centreId) {
                return centre;
            }
        }
        return null;
    }

    public FlipFitGymCenter getGymCentreByGymId(int gymId) {
        for (FlipFitGymCenter centre : gymCentres) {
            if (centre.getGymId() == gymId) {
                return centre;
            }
        }
        return null;
    }
}