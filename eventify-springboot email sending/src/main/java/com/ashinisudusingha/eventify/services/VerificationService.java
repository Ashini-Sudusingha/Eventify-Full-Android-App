package com.ashinisudusingha.eventify.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationService {

    private final EmailService emailService;
    private final JwtService jwtService;
    private final FirebaseAuth firebaseAuth;
    private final Firestore firestore;  // Firebase Firestore instance

    public boolean sendVerificationEmail(String userId, String email) {
        log.info("📧 Sending verification email to: {} with userId: {}", email, userId);

        try {
            // Generate JWT token
            String token = jwtService.generateVerificationToken(userId, email);

            if (token == null) {
                log.error("Failed to generate token");
                return false;
            }

            // Send email
            boolean emailSent = emailService.sendVerificationEmail(email, userId, token);

            if (emailSent) {
                log.info("✅ Verification email sent successfully to: {}", email);
                return true;
            } else {
                log.error("❌ Failed to send verification email to: {}", email);
                return false;
            }
        } catch (Exception e) {
            log.error("Error sending verification email: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean verifyEmail(String token) {
        try {
            // Verify JWT token
            if (!jwtService.verifyToken(token)) {
                log.warn("Invalid JWT token");
                return false;
            }

            String userId = jwtService.getUserIdFromToken(token);
            String email = jwtService.getEmailFromToken(token);

            if (userId == null || email == null) {
                log.error("User ID or Email not found in token");
                return false;
            }

            log.info("Verifying email for user: {} with email: {}", userId, email);

            // Update Firestore using Firebase SDK
            boolean firestoreUpdated = updateFirestoreVerification(userId);

            // Update Firebase Auth
            if (firestoreUpdated) {
                updateFirebaseAuthVerification(userId);
                log.info("✅ Email verified for user: {}", userId);
                return true;
            } else {
                log.error("❌ Failed to update verification status for user: {}", userId);
                return false;
            }

        } catch (Exception e) {
            log.error("Email verification failed: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Update Firestore using Firebase SDK (No REST API needed)
     */
    private boolean updateFirestoreVerification(String userId) {
        try {
            DocumentReference docRef = firestore.collection("users").document(userId);

            // Create update data
            Map<String, Object> updates = new HashMap<>();
            updates.put("emailVerified", true);
            updates.put("verifiedAt", System.currentTimeMillis());
            updates.put("verificationStatus", "VERIFIED");

            // Update the document
            ApiFuture<WriteResult> future = docRef.update(updates);
            WriteResult result = future.get();

            log.info("✅ Firestore updated for user: {} at: {}", userId, result.getUpdateTime());
            return true;

        } catch (InterruptedException | ExecutionException e) {
            log.error("❌ Firestore update failed: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Update Firebase Auth email verification status
     */
    private void updateFirebaseAuthVerification(String userId) {
        try {
            UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(userId)
                    .setEmailVerified(true);

            firebaseAuth.updateUser(request);
            log.info("✅ Firebase Auth updated for user: {}", userId);

        } catch (Exception e) {
            log.error("❌ Failed to update Firebase Auth: {}", e.getMessage(), e);
        }
    }

    public boolean isEmailVerified(String userId) {
        try {
            DocumentReference docRef = firestore.collection("users").document(userId);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                Boolean verified = document.getBoolean("emailVerified");
                return verified != null && verified;
            }
            return false;

        } catch (Exception e) {
            log.error("Error checking verification status: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean resendVerificationEmail(String userId, String email) {
        log.info("Resending verification email to user: {}", userId);
        return sendVerificationEmail(userId, email);
    }
}