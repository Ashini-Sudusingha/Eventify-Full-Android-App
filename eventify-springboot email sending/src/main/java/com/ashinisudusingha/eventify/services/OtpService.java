package com.ashinisudusingha.eventify.services;

import com.ashinisudusingha.eventify.dto.OtpResponse;
import com.ashinisudusingha.eventify.dto.VerifyOtpRequest;
import com.ashinisudusingha.eventify.model.OTPToken;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

@Service
public class OtpService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.name}")
    private String appName;

    private final FirebaseAuth firebaseAuth;
    private final Firestore firestore;

    public OtpService() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firestore = FirestoreClient.getFirestore();
    }


    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }


    public OtpResponse sendOtp(String email) {
        try {
            System.out.println("📧 Sending OTP to: " + email);

            // Check if user exists in Firebase Auth
            UserRecord userRecord = firebaseAuth.getUserByEmail(email);
            if (userRecord == null) {
                System.out.println("❌ User not found: " + email);
                return new OtpResponse(false, "Email address not found");
            }

            // Delete existing OTPs for this email
            deleteExistingOtps(email);

            // Generate new OTP
            String otpCode = generateOtp();
            System.out.println("🔑 Generated OTP: " + otpCode + " for: " + email);

            // Save OTP to Firestore
            OTPToken otpToken = new OTPToken(email, otpCode);
            saveOtpToFirestore(otpToken);

            // ✅ Send OTP via email directly
            boolean emailSent = sendOtpEmailDirect(email, otpCode);

            if (emailSent) {
                System.out.println("✅ OTP sent successfully to: " + email);
                return new OtpResponse(true, "OTP sent successfully to your email");
            } else {
                return new OtpResponse(false, "Failed to send OTP email");
            }

        } catch (FirebaseAuthException e) {
            System.out.println("❌ Firebase Auth error: " + e.getMessage());
            return new OtpResponse(false, "Email address not found");
        } catch (Exception e) {
            System.out.println("❌ Error sending OTP: " + e.getMessage());
            e.printStackTrace();
            return new OtpResponse(false, "Failed to send OTP: " + e.getMessage());
        }
    }


    private boolean sendOtpEmailDirect(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(appName + " - Password Reset OTP");

            String htmlContent = getOtpEmailHtml(otp);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("✅ OTP email sent to: " + toEmail);
            return true;

        } catch (MessagingException e) {
            System.out.println("❌ Failed to send email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String getOtpEmailHtml(String otp) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Password Reset OTP</title>
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        margin: 0;
                        padding: 0;
                        background-color: #f4f4f4;
                    }
                    .container {
                        max-width: 600px;
                        margin: 40px auto;
                        background: white;
                        border-radius: 10px;
                        overflow: hidden;
                        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                    }
                    .header {
                        background: linear-gradient(135deg, #00BBA7 0%, #009688 100%);
                        color: white;
                        padding: 30px;
                        text-align: center;
                    }
                    .header h1 {
                        margin: 0;
                        font-size: 28px;
                    }
                    .content {
                        padding: 40px 30px;
                        text-align: center;
                    }
                    .otp-code {
                        background: #f4f4f4;
                        padding: 20px;
                        text-align: center;
                        font-size: 36px;
                        letter-spacing: 5px;
                        font-weight: bold;
                        color: #00BBA7;
                        border-radius: 10px;
                        margin: 20px 0;
                        font-family: monospace;
                    }
                    .footer {
                        background: #f8f9fa;
                        padding: 20px;
                        text-align: center;
                        font-size: 12px;
                        color: #666;
                    }
                    .warning {
                        color: #ff6b6b;
                        font-size: 12px;
                        margin-top: 20px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🔐 Password Reset OTP</h1>
                    </div>
                    <div class="content">
                        <p>We received a request to reset your password.</p>
                        <p>Use the OTP below to reset your password:</p>
                        <div class="otp-code">""" + otp + """
                        </div>
                        <p>This OTP will expire in <strong>10 minutes</strong>.</p>
                        <div class="warning">
                            <strong>⚠️ Important:</strong> If you did not request this, please ignore this email.
                        </div>
                    </div>
                    <div class="footer">
                        <p>© 2024 Eventify. All rights reserved.</p>
                        <p>This is an automated message, please do not reply.</p>
                    </div>
                </div>
            </body>
            </html>
        """;
    }


    public OtpResponse verifyOtpAndResetPassword(VerifyOtpRequest request) {
        try {
            System.out.println("🔐 Verifying OTP for: " + request.getEmail());
            System.out.println("🔑 OTP: " + request.getOtp());

            // Get OTP from Firestore
            OTPToken otpToken = getOtpFromFirestore(request.getEmail(), request.getOtp());

            if (otpToken == null) {
                System.out.println("❌ Invalid OTP");
                return new OtpResponse(false, "Invalid OTP");
            }

            if (otpToken.isUsed()) {
                System.out.println("❌ OTP already used");
                return new OtpResponse(false, "OTP has already been used");
            }

            if (otpToken.isExpired()) {
                System.out.println("❌ OTP expired");
                return new OtpResponse(false, "OTP has expired. Please request a new one");
            }

            // Get user from Firebase Auth
            UserRecord userRecord = firebaseAuth.getUserByEmail(request.getEmail());

            // Update password in Firebase Auth
            UserRecord.UpdateRequest updateRequest = new UserRecord.UpdateRequest(userRecord.getUid())
                    .setPassword(request.getNewPassword());

            firebaseAuth.updateUser(updateRequest);
            System.out.println("✅ Password updated for: " + request.getEmail());

            // Mark OTP as used
            markOtpAsUsed(otpToken.getId());

            return new OtpResponse(true, "Password reset successfully");

        } catch (FirebaseAuthException e) {
            System.out.println("❌ Firebase Auth error: " + e.getMessage());
            return new OtpResponse(false, "User not found");
        } catch (Exception e) {
            System.out.println("❌ Error verifying OTP: " + e.getMessage());
            e.printStackTrace();
            return new OtpResponse(false, "Failed to reset password: " + e.getMessage());
        }
    }


    private void saveOtpToFirestore(OTPToken otpToken) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("otp_tokens").document();
        otpToken.setId(docRef.getId());

        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("email", otpToken.getEmail());
        tokenData.put("otp", otpToken.getOtp());
        tokenData.put("expiryDate", otpToken.getExpiryDate());
        tokenData.put("used", otpToken.isUsed());
        tokenData.put("createdAt", otpToken.getCreatedAt());

        docRef.set(tokenData).get();
        System.out.println("💾 OTP saved to Firestore: " + otpToken.getId());
    }


    private OTPToken getOtpFromFirestore(String email, String otp) throws ExecutionException, InterruptedException {
        Query query = firestore.collection("otp_tokens")
                .whereEqualTo("email", email)
                .whereEqualTo("otp", otp)
                .limit(1);

        QuerySnapshot snapshot = query.get().get();

        if (snapshot.isEmpty()) {
            return null;
        }

        DocumentSnapshot doc = snapshot.getDocuments().get(0);
        OTPToken otpToken = new OTPToken();
        otpToken.setId(doc.getId());
        otpToken.setEmail(doc.getString("email"));
        otpToken.setOtp(doc.getString("otp"));
        otpToken.setExpiryDate(doc.getDate("expiryDate"));
        otpToken.setUsed(doc.getBoolean("used") != null && doc.getBoolean("used"));
        otpToken.setCreatedAt(doc.getDate("createdAt"));

        return otpToken;
    }


    private void deleteExistingOtps(String email) throws ExecutionException, InterruptedException {
        Query query = firestore.collection("otp_tokens").whereEqualTo("email", email);
        QuerySnapshot snapshot = query.get().get();

        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            doc.getReference().delete().get();
            System.out.println("🗑️ Deleted old OTP for: " + email);
        }
    }

    private void markOtpAsUsed(String documentId) throws ExecutionException, InterruptedException {
        firestore.collection("otp_tokens").document(documentId).update("used", true).get();
        System.out.println("✅ OTP marked as used: " + documentId);
    }
}