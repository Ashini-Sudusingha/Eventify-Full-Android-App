package com.ashinisudusingha.eventify.model;

import lombok.Data;

@Data
public class VerificationRequest {
    private String userId;
    private String email;

    public VerificationRequest() {}

    public VerificationRequest(String userId, String email) {
        this.userId = userId;
        this.email = email;
    }
}