package com.flipfit.dao;

import com.flipfit.bean.FlipFitGymCenter;
import java.util.ArrayList;
import java.util.List;

public class GymCentreDAO {

    private static GymCentreDAO instance = null;
    private final List<FlipFitGymCenter> gymCentres = new ArrayList<>();

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

    public List<FlipFitGymCenter> getGymCentres() {
        return gymCentres;
    }
}
