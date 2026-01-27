package com.flipfit.bean;

public class FlipFitCustomer extends FlipFitUser {

    // From class diagram
    private int paymentType;     // e.g. 1 = Card, 2 = UPI
    private String paymentInfo;  // card/upi details
    private String contact;      // phone/email contact

    public FlipFitCustomer(int userId, String fullName) {
        this.userId = userId;
        this.fullName = fullName;
        this.role = "CUSTOMER";
        this.contact = "N/A";
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

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
    	 return "Customer [Id=" + userId +
                 ", Name=" + fullName +
                 ", Contact=" + contact +
                 ", PaymentType=" + paymentType + "]";
      }
  }

