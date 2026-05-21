// OTPToken.java
package com.ashinisudusingha.eventify.model;

import java.util.Date;

public class OTPToken {
    private static final int EXPIRATION = 10 * 60 * 1000; // 10 minutes in milliseconds

    private String id;
    private String email;
    private String otp;
    private Date expiryDate;
    private boolean used;
    private Date createdAt;

    public OTPToken() {}

    public OTPToken(String email, String otp) {
        this.email = email;
        this.otp = otp;
        this.createdAt = new Date();
        this.expiryDate = new Date(System.currentTimeMillis() + EXPIRATION);
        this.used = false;
    }

    public boolean isExpired() {
        return new Date().after(this.expiryDate);
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }
    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}