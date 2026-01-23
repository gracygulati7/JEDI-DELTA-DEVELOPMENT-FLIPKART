package com.flipfit.bean;

public class FlipFitUser {
    private int userId;
    private String fullName;
    private String email;
    private String password; 
    private long phoneNumber;
    private String city;
    private int pincode;
    private String role;

    public FlipFitUser() {}

    public FlipFitUser(int userId, String fullName, String email, String password, String role) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public int getUserId() { 
    	return userId; 
    }
    public void setUserId(int userId) { 
    	this.userId = userId; 
    	}
    public String getFullName() { 
    	return fullName; }
    public void setFullName(String fullName) { 
    	this.fullName = fullName; }
    public String getEmail() { 
    	return email; }
    public void setEmail(String email) { 
    	this.email = email; }
    public String getPassword() { 
    	return password; }
    public void setPassword(String password) { 
    	this.password = password; }
    public long getPhoneNumber() { 
    	return phoneNumber; }
    public void setPhoneNumber(long phoneNumber) { 
    	this.phoneNumber = phoneNumber; }
    public String getCity() { 
    	return city; }
    public void setCity(String city) { 
    	this.city = city; }
    public int getPincode() { 
    	return pincode; }
    public void setPincode(int pincode) { 
    	this.pincode = pincode; }
    public String getRole() { 
    	return role; }
    public void setRole(String role) { 
    	this.role = role; }
}
