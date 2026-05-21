package com.ashinisudusingha.eventify.controllers;

import com.ashinisudusingha.eventify.model.EmailInvitationRequest;
import com.ashinisudusingha.eventify.model.EmailInvitationResponse;
import com.ashinisudusingha.eventify.services.EmailInvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "*")
public class EmailInvitationController {

    @Autowired
    private EmailInvitationService  emailService;

    @PostMapping("/send-invitation")
    public ResponseEntity<EmailInvitationResponse> sendInvitation(
            @RequestBody EmailInvitationRequest request) {
        try {
            boolean sent = emailService.sendInvitationEmail(request);
            if (sent) {
                return ResponseEntity.ok(new EmailInvitationResponse(
                        true,
                        "Email sent successfully",
                        request.getEventGuestId(),
                        "Sent"
                ));
            } else {
                return ResponseEntity.badRequest().body(new EmailInvitationResponse(
                        false,
                        "Failed to send email",
                        request.getEventGuestId(),
                        "Failed"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new EmailInvitationResponse(false, "Error: " + e.getMessage(), null, "Error"));
        }
    }

    @PostMapping("/send-bulk-invitations")
    public ResponseEntity<EmailInvitationResponse> sendBulkInvitations(
            @RequestBody List<EmailInvitationRequest> requests) {
        try {
            int successCount = 0;
            int failCount = 0;
            for (EmailInvitationRequest request : requests) {
                if (emailService.sendInvitationEmail(request)) {
                    successCount++;
                } else {
                    failCount++;
                }
            }
            return ResponseEntity.ok(new EmailInvitationResponse(
                    true,
                    "Sent: " + successCount + ", Failed: " + failCount,
                    null,
                    null
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new EmailInvitationResponse(false, "Error: " + e.getMessage(), null, null));
        }
    }
}