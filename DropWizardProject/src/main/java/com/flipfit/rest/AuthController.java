/**
 * ============================================================================
 * 1. INTEGRATED AUTH CONTROLLER (rest/AuthController.java)
 * ============================================================================
 */
package com.flipfit.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.flipfit.dao.CustomerDAO;
import com.flipfit.dao.OwnerDAO;
import com.flipfit.dao.AdminDAO;
import com.flipfit.bean.FlipFitGymOwner;
import com.flipfit.bean.FlipFitCustomer;
import com.flipfit.business.GymOwnerService;
import com.flipfit.business.GymOwnerServiceImpl;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {
    
    private GymOwnerService gymOwnerService = new GymOwnerServiceImpl();
    
    /**
     * Register a new gym owner
     */
    @POST
    @Path("/register/owner")
    public Response registerOwner(OwnerRegistrationRequest request) {
        try {
            // This service call should insert into 'users' table THEN 'Owner' table
            gymOwnerService.registerOwner(
                request.getName(), 
                request.getEmail(), 
                request.getPassword(), 
                request.getPan(), 
                request.getAadhaar(), 
                request.getGstin()
            );
            
            return Response.status(Response.Status.CREATED)
                .entity(new ApiResponse(true, "Gym Owner Registration successful! Note: Your account is pending admin approval."))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ApiResponse(false, "Registration failed: " + e.getMessage()))
                .build();
        }
    }

    /**
     * Register a new customer
     */
    @POST
    @Path("/register/customer")
    public Response registerCustomer(CustomerRegistrationRequest request) {
        try {
            // UPDATED: Now passing email and password variables so they aren't hardcoded in DAO
            CustomerDAO customerDAO = CustomerDAO.getInstance();
            FlipFitCustomer customer = customerDAO.addCustomer(
                request.getName(), 
                request.getEmail(), 
                request.getPassword()
            );
            
            // Update the dummy values with actual registration data
            customer.setContact(request.getContact());
            customerDAO.updateCustomer(customer);
            
            return Response.status(Response.Status.CREATED)
                .entity(new ApiResponse(true, "Customer registration successful! You can now login with email: " + request.getEmail()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ApiResponse(false, "Registration failed: " + e.getMessage()))
                .build();
        }
    }
    
    /**
     * Login endpoint supporting all three roles
     */
    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        try {
            switch (request.getRole()) {
                case "owner":
                    return loginOwner(request.getEmail(), request.getPassword());
                case "customer":
                    return loginCustomer(request.getEmail(), request.getPassword());
                case "admin":
                    return loginAdmin(request.getEmail(), request.getPassword());
                default:
                    return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ApiResponse(false, "Invalid role selected"))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ApiResponse(false, "Login failed: " + e.getMessage()))
                .build();
        }
    }
    
    private Response loginOwner(String email, String password) {
        OwnerDAO ownerDAO = OwnerDAO.getInstance();
        FlipFitGymOwner owner = ownerDAO.getOwnerByEmail(email);
        
        if (owner == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ApiResponse(false, "Gym Owner account not found for email: " + email))
                .build();
        }
        if (!owner.isApproved()) {
            return Response.status(Response.Status.FORBIDDEN)
                .entity(new ApiResponse(false, "Your account is still pending admin approval."))
                .build();
        }
        
        return Response.ok()
            .entity(new LoginResponse(true, "Logged in as Gym Owner: " + owner.getName(), 
                owner.getOwnerId(), "owner", owner))
            .build();
    }
    
    private Response loginCustomer(String email, String password) {
        CustomerDAO customerDAO = CustomerDAO.getInstance();
        // UPDATED: Search by Email instead of Name to fix login failure
        FlipFitCustomer customer = customerDAO.getCustomerByEmail(email);
        
        if (customer == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ApiResponse(false, "Customer account not found. Please register."))
                .build();
        }
        
        return Response.ok()
            .entity(new LoginResponse(true, "Logged in as Gym Customer: " + customer.getFullName(), 
                customer.getUserId(), "customer", customer))
            .build();
    }
    
    private Response loginAdmin(String email, String password) {
        AdminDAO adminDAO = AdminDAO.getInstance();
        if (!adminDAO.login(email, password)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ApiResponse(false, "Invalid admin credentials"))
                .build();
        }
        
        return Response.ok()
            .entity(new LoginResponse(true, "Logged in as Gym Admin", null, "admin", null))
            .build();
    }
    
    // Inner classes for request/response DTOs
    public static class OwnerRegistrationRequest {
        private String name;
        private String email;
        private String password;
        private String pan;
        private String aadhaar;
        private String gstin;
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getPan() { return pan; }
        public void setPan(String pan) { this.pan = pan; }
        public String getAadhaar() { return aadhaar; }
        public void setAadhaar(String aadhaar) { this.aadhaar = aadhaar; }
        public String getGstin() { return gstin; }
        public void setGstin(String gstin) { this.gstin = gstin; }
    }
    
    public static class CustomerRegistrationRequest {
        private String name;
        private String email;
        private String password;
        private String contact;
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getContact() { return contact; }
        public void setContact(String contact) { this.contact = contact; }
    }
    
    public static class LoginRequest {
        private String email;
        private String password;
        private String role; // "owner", "customer", "admin"
        
        // Getters and setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
    
    public static class ApiResponse {
        private boolean success;
        private String message;
        
        public ApiResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    public static class LoginResponse extends ApiResponse {
        private Integer userId;
        private String role;
        private Object userData;
        
        public LoginResponse(boolean success, String message, Integer userId, String role, Object userData) {
            super(success, message);
            this.userId = userId;
            this.role = role;
            this.userData = userData;
        }
        
        public Integer getUserId() { return userId; }
        public void setUserId(Integer userId) { this.userId = userId; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public Object getUserData() { return userData; }
        public void setUserData(Object userData) { this.userData = userData; }
    }
}
