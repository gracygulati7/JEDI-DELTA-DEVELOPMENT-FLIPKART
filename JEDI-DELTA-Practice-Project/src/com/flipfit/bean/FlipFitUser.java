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

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public int getPincode() { return pincode; }
    public void setPincode(int pincode) { this.pincode = pincode; }

}