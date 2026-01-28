package com.flipfit.business;

import com.flipfit.bean.Slot;
import com.flipfit.dao.SlotDAO;
import com.flipfit.exceptions.DbConnectionException;
import java.util.List;

public class GymCentreServiceImpl implements GymCentreService {

    private final SlotDAO slotDAO = SlotDAO.getInstance();

    @Override
    public List<Slot> getSlotsByCentreId(int centreId) throws DbConnectionException {
        // Calls DAO which now throws Checked Exception
        return slotDAO.getSlotsByCenterId(centreId);
    }
}