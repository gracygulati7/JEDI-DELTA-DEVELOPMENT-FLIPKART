package com.flipfit.business;

import com.flipfit.exceptions.DbConnectionException;

public interface NotificationService {
    // These methods query the DB to get names/times, so they might fail with DB error
    void sendBookingConfirmation(int userId, int slotId, int centerId) throws DbConnectionException;

    void sendWaitlistPromotion(int userId, int slotId, int centerId) throws DbConnectionException;
    
    void sendCancellationNotification(int userId, int slotId, int centerId) throws DbConnectionException;
    
    void sendConflictWarning(int userId, String message); // In-memory only, no DB exception needed
    
    void sendSlotFullNotification(int userId, int slotId, int centerId) throws DbConnectionException;
}