package com.flipfit.bean;

public class FlipFitCustomer extends FlipFitUser{
	private int paymentType;
	public int getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(int paymentType) {
		this.paymentType = paymentType;
	}
	public String getPaymentInfo() {
		return paymentInfo;
	}
	public void setPaymentInfo(String paymentInfo) {
		this.paymentInfo = paymentInfo;
	}
	private String paymentInfo;
	
}
