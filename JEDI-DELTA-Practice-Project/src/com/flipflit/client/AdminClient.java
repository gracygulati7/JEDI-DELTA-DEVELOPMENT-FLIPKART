package com.flipflit.client;

import com.flipfit.business.AdminService;
import com.flipfit.business.AdminServiceImpl;


public class AdminClient {
	public static void main(String[] args) {
		AdminService adminService = new AdminServiceImpl();
		
		adminService.validateOwner(201);
	}
	
}
