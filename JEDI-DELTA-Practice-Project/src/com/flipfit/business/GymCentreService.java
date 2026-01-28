package com.flipfit.business;

import com.flipfit.bean.Slot;
import com.flipfit.exceptions.DbConnectionException;
import java.util.List;

public interface GymCentreService {
    // Added throws DbConnectionException to match the DAO
    List<Slot> getSlotsByCentreId(int centreId) throws DbConnectionException;
}