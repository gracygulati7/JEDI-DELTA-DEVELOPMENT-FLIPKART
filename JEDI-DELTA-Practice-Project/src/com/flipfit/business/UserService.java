package com.flipfit.business;

import com.flipfit.bean.Slot;
import com.flipfit.exceptions.DbConnectionException;
import com.flipfit.exceptions.UserNotFoundException;

import java.util.List;

public interface UserService {

    void viewProfile(int userId) throws DbConnectionException, UserNotFoundException;

    void editProfile(int userId) throws DbConnectionException, UserNotFoundException;

    List<Slot> findAvailableSlots(int centreId) throws DbConnectionException;
}