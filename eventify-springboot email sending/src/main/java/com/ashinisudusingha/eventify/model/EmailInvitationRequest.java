package com.ashinisudusingha.eventify.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailInvitationRequest {
    private String to;
    private String subject;
    private String htmlContent;
    private String eventId;
    private String guestId;
    private String eventGuestId;
    private String qrCode;
    private String eventName;
    private String eventDate;
    private String eventLocation;
    private String locationLatitude;
    private String locationLongitude;
    private String invitationStatus;
}