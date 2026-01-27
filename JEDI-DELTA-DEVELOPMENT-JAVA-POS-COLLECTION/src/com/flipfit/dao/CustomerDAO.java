package com.flipfit.dao;

import com.flipfit.bean.FlipFitCustomer;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

public class CustomerDAO {
    private static CustomerDAO instance = null;
    private final Map<Integer, FlipFitCustomer> customers = new HashMap<>();
    private int nextUserId = 201;

    private CustomerDAO() {}

    public static CustomerDAO getInstance() {
        if (instance == null) {
            instance = new CustomerDAO();
        }
        return instance;
    }

    public FlipFitCustomer addCustomer(String fullName) {
        FlipFitCustomer c = new FlipFitCustomer(nextUserId++, fullName);
        customers.put(c.getUserId(), c);
        return c;
    }

    public FlipFitCustomer getCustomerByName(String name) {
        for (FlipFitCustomer c : customers.values()) {
            if (c.getFullName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

    public FlipFitCustomer getOrCreateCustomerByName(String name) {
        FlipFitCustomer c = getCustomerByName(name);
        if (c != null) return c;
        return addCustomer(name);
    }

    public FlipFitCustomer getCustomerById(int id) {
        return customers.get(id);
    }

    public Collection<FlipFitCustomer> getAllCustomers() {
        return customers.values();
    }
    
    public void updateCustomer(FlipFitCustomer user) {
    	if(user!=null && customers.containsKey(user.getUserId())) {
    		customers.put(user.getUserId(), user);
    	}
    }
    
    public void updatePaymentDetails(int userId, int paymentType, String paymentInfo) {
        FlipFitCustomer customer = customers.get(userId);
        if (customer != null) {
            customer.setPaymentType(paymentType);
            customer.setPaymentInfo(paymentInfo);
        }
    }
}
