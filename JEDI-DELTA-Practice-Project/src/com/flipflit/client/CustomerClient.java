package com.flipflit.client;

import com.flipfit.business.CustomerService;
import com.flipfit.business.CustomerServiceImpl;

public class CustomerClient {
	public static void main(String[]args) {
		CustomerService customerService = new CustomerServiceImpl();
		
		customerService.viewCenters();
		customerService.makeBooking(101,5);
	}
}
