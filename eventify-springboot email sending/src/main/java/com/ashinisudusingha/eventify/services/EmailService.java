package com.ashinisudusingha.eventify.services;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;


    public boolean sendVerificationEmail(String toEmail, String userId, String token) {
        try {
            String verificationLink = frontendUrl + "/api/verify?token=" + token + "&userId=" + userId;

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Fix: Use InternetAddress to handle encoding properly
            helper.setFrom(new InternetAddress(fromEmail, "Eventify Team"));
            helper.setTo(toEmail);
            helper.setSubject("Verify Your Email - Eventify");
            helper.setText(getVerificationEmailHtml(verificationLink, userId), true);

            mailSender.send(message);
            log.info("✅ Verification email sent to: {}", toEmail);
            return true;

        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("❌ Failed to send email to {}: {}", toEmail, e.getMessage());
            return false;
        }
    }

//test
    public boolean sendTestEmail(String toEmail) {
        try {
            log.info("📧 Sending test email to: {}", toEmail);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(new InternetAddress(fromEmail, "Eventify Team"));
            helper.setTo(toEmail);
            helper.setSubject("Test Email from Eventify");
            helper.setText("""
            <!DOCTYPE html>
            <html>
            <body>
                <h2>Test Email</h2>
                <p>If you receive this email, your email configuration is working correctly!</p>
                <p>Time: """ + new Date() + """
            </p>
            </body>
            </html>
        """, true);

            mailSender.send(message);
            log.info("✅ Test email sent successfully to: {}", toEmail);
            return true;

        } catch (Exception e) {
            log.error("❌ Test email failed: {}", e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public boolean sendWelcomeEmail(String toEmail, String userName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(new InternetAddress(fromEmail, "Eventify Team"));
            helper.setTo(toEmail);
            helper.setSubject("Welcome to Eventify! 🎉");
            helper.setText(getWelcomeEmailHtml(userName), true);

            mailSender.send(message);
            log.info("✅ Welcome email sent to: {}", toEmail);
            return true;

        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("❌ Failed to send welcome email: {}", e.getMessage());
            return false;
        }
    }


    public boolean sendPasswordResetEmail(String toEmail, String resetToken) {
        try {
            String resetLink = frontendUrl + "/api/reset-password?token=" + resetToken;

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(new InternetAddress(fromEmail, "Eventify Team"));
            helper.setTo(toEmail);
            helper.setSubject("Reset Your Password - Eventify");
            helper.setText(getPasswordResetEmailHtml(resetLink), true);

            mailSender.send(message);
            log.info("✅ Password reset email sent to: {}", toEmail);
            return true;

        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("❌ Failed to send password reset email: {}", e.getMessage());
            return false;
        }
    }


    private String getVerificationEmailHtml(String verificationLink, String userId) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Verify Your Email</title>
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
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
                    .button {
                        display: inline-block;
                        padding: 14px 35px;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: white;
                        text-decoration: none;
                        border-radius: 5px;
                        margin: 25px 0;
                        font-weight: bold;
                        font-size: 16px;
                    }
                    .button:hover {
                        opacity: 0.9;
                    }
                    .footer {
                        background: #f8f9fa;
                        padding: 20px;
                        text-align: center;
                        font-size: 12px;
                        color: #666;
                    }
                    .link-box {
                        background: #f0f0f0;
                        padding: 12px;
                        border-radius: 5px;
                        margin: 15px 0;
                        word-break: break-all;
                        font-size: 12px;
                        color: #667eea;
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
                        <h1>🎉 Eventify</h1>
                        <p>Your Event Management Partner</p>
                    </div>
                    <div class="content">
                        <h2>Verify Your Email Address</h2>
                        <p>Thank you for signing up with <strong>Eventify</strong>!</p>
                        <p>Please click the button below to verify your email address and activate your account.</p>
                        
                        <a href=\"""" + verificationLink + "\" class=\"button\">✅ Verify Email Address</a>\n" +
                """
                <p>Or copy and paste this link into your browser:</p>
                <div class="link-box">""" + verificationLink +
                        """
                        </div>
                        
                        <div class="warning">
                            <strong>⚠️ Note:</strong> This link will expire in <strong>1 hour</strong>.<br>
                            If you didn't create an account with Eventify, please ignore this email.
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


    private String getWelcomeEmailHtml(String userName) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Welcome to Eventify</title>
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
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: white;
                        padding: 30px;
                        text-align: center;
                    }
                    .content {
                        padding: 40px 30px;
                        text-align: center;
                    }
                    .footer {
                        background: #f8f9fa;
                        padding: 20px;
                        text-align: center;
                        font-size: 12px;
                        color: #666;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎉 Welcome to Eventify!</h1>
                    </div>
                    <div class="content">
                        <h2>Hello """ + userName +
                        """
                        !</h2>
                        <p>Your email has been successfully verified.</p>
                        <p>You can now start using Eventify to manage your events.</p>
                        <p>Here are some things you can do:</p>
                        <ul style="text-align: left;">
                            <li>📅 Create and manage events</li>
                            <li>🎫 Generate QR codes for tickets</li>
                            <li>📊 Track event analytics</li>
                            <li>👥 Manage attendees</li>
                        </ul>
                        <p>If you have any questions, feel free to contact our support team.</p>
                        <p>Happy Event Planning! 🎊</p>
                    </div>
                    <div class="footer">
                        <p>© 2024 Eventify. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
        """;
    }


    private String getPasswordResetEmailHtml(String resetLink) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Reset Your Password</title>
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
                        background: linear-gradient(135deg, #ff6b6b 0%, #c92a2a 100%);
                        color: white;
                        padding: 30px;
                        text-align: center;
                    }
                    .content {
                        padding: 40px 30px;
                        text-align: center;
                    }
                    .button {
                        display: inline-block;
                        padding: 14px 35px;
                        background: linear-gradient(135deg, #ff6b6b 0%, #c92a2a 100%);
                        color: white;
                        text-decoration: none;
                        border-radius: 5px;
                        margin: 25px 0;
                        font-weight: bold;
                    }
                    .footer {
                        background: #f8f9fa;
                        padding: 20px;
                        text-align: center;
                        font-size: 12px;
                        color: #666;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🔐 Reset Your Password</h1>
                    </div>
                    <div class="content">
                        <p>We received a request to reset your password.</p>
                        <p>Click the button below to create a new password:</p>
                        
                        <a href=\"""" + resetLink + "\" class=\"button\">Reset Password</a>\n" +
                """
                <p>If you didn't request this, please ignore this email.</p>
                <p>This link will expire in 1 hour.</p>
            </div>
            <div class="footer">
                <p>© 2024 Eventify. All rights reserved.</p>
            </div>
        </div>
    </body>
    </html>
""";
    }
}