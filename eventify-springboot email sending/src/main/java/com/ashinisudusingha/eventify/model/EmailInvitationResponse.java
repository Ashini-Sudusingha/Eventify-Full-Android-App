package com.ashinisudusingha.eventify.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailInvitationResponse {
    private boolean success;
    private String message;
    private String eventGuestId;
    private String invitationStatus;
}