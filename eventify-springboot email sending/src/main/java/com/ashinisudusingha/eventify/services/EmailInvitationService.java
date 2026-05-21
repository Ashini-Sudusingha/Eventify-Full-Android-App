package com.ashinisudusingha.eventify.services;

import com.ashinisudusingha.eventify.model.EmailInvitationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;  // Spring Boot 3.x uses jakarta.mail
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailInvitationService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public boolean sendInvitationEmail(EmailInvitationRequest request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(request.getTo());
            helper.setSubject(request.getSubject());
            helper.setText(request.getHtmlContent(), true);

            mailSender.send(message);
            logger.info("Email sent successfully to: {}", request.getTo());
            return true;
        } catch (Exception e) {
            logger.error("Failed to send email to: {}", request.getTo(), e);
            return false;
        }
    }
}