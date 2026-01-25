package com.flipfit.bean;

public class FlipFitGymOwner {

    private int ownerId;
    private String name;
    private String pan;
    private String aadhaar;
    private String gstin;
    private boolean isValidated;

    public FlipFitGymOwner(int ownerId, String name, String pan, String aadhaar, String gstin) {
        this.ownerId = ownerId;
        this.name = name;
        this.pan = pan;
        this.aadhaar = aadhaar;
        this.gstin = gstin;
        this.isValidated = false;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getName() {
        return name;
    }

    public String getPan() {
        return pan;
    }

    public String getAadhaar() {
        return aadhaar;
    }

    public String getGstin() {
        return gstin;
    }

    public boolean isValidated() {
        return isValidated;
    }

    public void setValidated(boolean validated) {
        isValidated = validated;
    }

    @Override
    public String toString() {
        return "GymOwner [OwnerId=" + ownerId + ", Name=" + name + ", IsValidated=" + isValidated + "]";
    }
}