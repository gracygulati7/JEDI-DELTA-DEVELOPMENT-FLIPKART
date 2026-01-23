package com.flipfit.bean;

public class FlipFitGymOwner extends FlipFitUser {
	
	private String PAN;
	public String getPAN() {
		return PAN;
	}
	public void setPAN(String pAN) {
		PAN = pAN;
	}
	public String getAadhar() {
		return Aadhar;
	}
	public void setAadhar(String aadhar) {
		Aadhar = aadhar;
	}
	public String getGSTIN() {
		return GSTIN;
	}
	public void setGSTIN(String gSTIN) {
		GSTIN = gSTIN;
	}
	private String Aadhar;
	private String GSTIN;

}
