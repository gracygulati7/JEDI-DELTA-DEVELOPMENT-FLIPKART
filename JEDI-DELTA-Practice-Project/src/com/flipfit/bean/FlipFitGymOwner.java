package com.flipfit.bean;

public class FlipFitGymOwner  extends FlipFitUser{

    private String pan;
    private String aadhaar;
    private String gstin;
    private boolean isValidated;

    public FlipFitGymOwner(int ownerId, String name, String pan, String aadhaar, String gstin) {
        this.userId = ownerId;
        this.fullName = name;
        this.pan = pan;
        this.aadhaar = aadhaar;
        this.gstin = gstin;
        this.isValidated = false;
    }

    public int getOwnerId() {
        return this.userId;
    }

    public String getName() {
        return this.fullName;
    }

    public boolean isValidated() {
        return isValidated;
    }

    public void setValidated(boolean validated) {
        isValidated = validated;
    }
}