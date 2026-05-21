
package com.ashinisudusingha.eventify.controllers;

import com.ashinisudusingha.eventify.dto.OtpResponse;
import com.ashinisudusingha.eventify.dto.SendOtpRequest;
import com.ashinisudusingha.eventify.dto.VerifyOtpRequest;
import com.ashinisudusingha.eventify.services.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/otp")
@CrossOrigin(origins = "*")
public class OtpController {

    @Autowired
    private OtpService otpService;


    @PostMapping("/send")
    public ResponseEntity<OtpResponse> sendOtp(@RequestBody SendOtpRequest request) {
        OtpResponse response = otpService.sendOtp(request.getEmail());
        return ResponseEntity.ok(response);
    }


    @PostMapping("/verify")
    public ResponseEntity<OtpResponse> verifyOtpAndReset(@RequestBody VerifyOtpRequest request) {
        OtpResponse response = otpService.verifyOtpAndResetPassword(request);
        return ResponseEntity.ok(response);
    }
}