package com.ashinisudusingha.eventify.model;

import lombok.Data;

@Data
public class VerificationResponse {
    private boolean success;
    private String message;

    public VerificationResponse() {}

    public VerificationResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}