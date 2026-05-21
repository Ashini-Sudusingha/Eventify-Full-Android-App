package com.ashinisudusingha.eventify.controllers;


import com.ashinisudusingha.eventify.model.VerificationRequest;
import com.ashinisudusingha.eventify.model.VerificationResponse;
import com.ashinisudusingha.eventify.services.EmailService;
import com.ashinisudusingha.eventify.services.VerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;
    private final EmailService emailService;

    @PostMapping("/send-verification")
    public ResponseEntity<VerificationResponse> sendVerificationEmail(@RequestBody VerificationRequest request) {
        log.info("📧 Sending verification email to: {}", request.getEmail());
        log.info("📧 User ID: {}", request.getUserId());

        try {
            boolean success = verificationService.sendVerificationEmail(request.getUserId(), request.getEmail());

            if (success) {
                log.info("✅ Verification email sent successfully to: {}", request.getEmail());
                return ResponseEntity.ok(new VerificationResponse(true, "Verification email sent successfully"));
            } else {
                log.error("❌ Failed to send verification email to: {}", request.getEmail());
                return ResponseEntity.badRequest().body(new VerificationResponse(false, "Failed to send verification email"));
            }
        } catch (Exception e) {
            log.error("❌ Error sending verification email: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new VerificationResponse(false, "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<VerificationResponse> verifyEmail(@RequestParam String token) {
        log.info("🔐 Verifying email with token: {}", token);

        try {
            boolean success = verificationService.verifyEmail(token);

            if (success) {
                log.info("✅ Email verified successfully");
                return ResponseEntity.ok(new VerificationResponse(true, "Email verified successfully"));
            } else {
                log.error("❌ Invalid or expired token");
                return ResponseEntity.badRequest().body(new VerificationResponse(false, "Invalid or expired verification token"));
            }
        } catch (Exception e) {
            log.error("❌ Verification error: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(new VerificationResponse(false, "Error: " + e.getMessage()));
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<VerificationResponse> resendVerificationEmail(@RequestBody VerificationRequest request) {
        log.info("🔄 Resending verification email to: {}", request.getEmail());
        log.info("🔄 User ID: {}", request.getUserId());

        try {
            // Validate input
            if (request.getUserId() == null || request.getUserId().isEmpty()) {
                log.error("❌ User ID is null or empty");
                return ResponseEntity.badRequest().body(new VerificationResponse(false, "User ID is required"));
            }

            if (request.getEmail() == null || request.getEmail().isEmpty()) {
                log.error("❌ Email is null or empty");
                return ResponseEntity.badRequest().body(new VerificationResponse(false, "Email is required"));
            }

            boolean success = verificationService.resendVerificationEmail(request.getUserId(), request.getEmail());

            if (success) {
                log.info("✅ Verification email resent successfully to: {}", request.getEmail());
                return ResponseEntity.ok(new VerificationResponse(true, "Verification email resent successfully"));
            } else {
                log.error("❌ Failed to resend verification email to: {}", request.getEmail());
                return ResponseEntity.badRequest().body(new VerificationResponse(false, "Failed to resend verification email"));
            }
        } catch (Exception e) {
            log.error("❌ Error resending verification email: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new VerificationResponse(false, "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/test-email")
    public ResponseEntity<String> testEmail(@RequestParam String email) {
        log.info("📧 Testing email to: {}", email);

        try {
            boolean sent = emailService.sendTestEmail(email);
            if (sent) {
                return ResponseEntity.ok("Test email sent successfully to " + email);
            } else {
                return ResponseEntity.badRequest().body("Failed to send test email");
            }
        } catch (Exception e) {
            log.error("❌ Test email error: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Email verification service is running!");
    }
}