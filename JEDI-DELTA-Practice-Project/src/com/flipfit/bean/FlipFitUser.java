package com.flipfit.bean;

public class FlipFitUser {

    protected int userId;
    protected String fullName;
    protected String email;
    protected String password;
    protected long phoneNumber;
    protected String city;
    protected int pincode;
    protected String role;

    public FlipFitUser() {}

    public FlipFitUser(int userId, String fullName) {
        this.userId = userId;
        this.fullName = fullName;
    }

    public int getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }
    public String getRole() {
    	return role;
    }
    public void setRole(String role) {
    	this.role=role;
    }
}