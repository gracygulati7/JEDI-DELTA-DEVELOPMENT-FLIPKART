package com.flipfit.bean;

import java.util.Queue;
import java.util.LinkedList;

public class Waitlist {

    private int waitlistId;
    private int slotId;
    private Queue<Integer> customerQueue = new LinkedList<>(); 

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

    public void setCustomerQueue(Queue<Integer> customerQueue) {
        this.customerQueue = customerQueue;
    }
}
