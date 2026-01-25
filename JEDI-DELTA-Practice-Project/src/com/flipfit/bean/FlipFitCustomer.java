package com.flipfit.bean;

public class FlipFitCustomer extends FlipFitUser {

    // From class diagram
    private int paymentType;     // e.g. 1 = Card, 2 = UPI
    private String paymentInfo;  // card/upi details

    public FlipFitCustomer(int userId, String fullName, int pincode, String city) {
        this.userId = userId;
        this.fullName = fullName;
        this.role = "CUSTOMER";
        this.pincode = pincode;
        this.city = city ;
    }

    // --- getters & setters ---

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

    @Override
    public String toString() {
    	 return "Customer [Id=" + userId +
                 ", Name=" + fullName +
                 ", PaymentType=" + paymentType + "]";
      }
  }

