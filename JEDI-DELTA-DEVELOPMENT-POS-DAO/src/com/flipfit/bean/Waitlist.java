package com.flipfit.bean;

import java.util.Queue;
import java.util.LinkedList;

public class Waitlist {

    private int waitlistId;
    private int slotId;
    private Queue<Integer> customerQueue = new LinkedList<>(); 

    // Constructor 
    public Waitlist(int waitlistId, int slotId) {
        this.waitlistId = waitlistId;
        this.slotId = slotId;
    }

    // Adds a customer 
    public void addCustomer(int userId) {
        customerQueue.add(userId);
    }

    // Removes and returns the next customer
    public Integer getNextInLine() {
        return customerQueue.poll();
    }

    // Checks if empty
    public boolean isEmpty() {
        return customerQueue.isEmpty();
    }

    public int getWaitlistId() {
        return waitlistId;
    }

    public void setWaitlistId(int waitlistId) {
        this.waitlistId = waitlistId;
    }

    public int getSlotId() {
        return slotId;
    }

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public Queue<Integer> getCustomerQueue() {
        return customerQueue;
    }
}