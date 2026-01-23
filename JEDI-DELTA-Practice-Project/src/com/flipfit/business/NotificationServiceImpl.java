package com.flipfit.business;

public class NotificationServiceImpl implements NotificationService{
    public void sendBookingConfirmation(int userId, int slotId) {
        System.out.println("Notification: Booking confirmed for User " + userId + " at Slot " + slotId);
    }

    public void sendWaitlistPromotion(int userId, int slotId) {
        System.out.println("Notification: You have moved from Waitlist to Confirmed!");
    }
}
