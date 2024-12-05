package com.ss.controller;

import com.ss.domain.VerificationType;
import com.ss.model.User;
import com.ss.model.VerificationCode;
import com.ss.service.EmailService;
import com.ss.service.UserService;
import com.ss.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationCodeService verificationCodeService;

    //get user by jwt
    @GetMapping("/api/users/profile")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String  jwt) throws Exception {
        User user  = userService.findUserProfileByJwt(jwt);
        return new ResponseEntity<User>(user, HttpStatus.OK);

    }


    @PostMapping("/api/users/verification/{verificationType}/send-otp")
    public ResponseEntity<String> sendVerificationOtp(
            @RequestHeader("Authorization") String  jwt,
            @PathVariable VerificationType verificationType) throws Exception {
        User user  = userService.findUserProfileByJwt(jwt);

        VerificationCode verificationCode = verificationCodeService
                .getVerificationCodeByUser(user.getId());

        if (verificationCode == null) {
        verificationCode = verificationCodeService
                .sendVerificationCode(user,verificationType);
        }

        if (verificationType.equals(VerificationType.EMAIL)) {
            emailService.sendVerificationOTPEmail(user.getEmail(), verificationCode.getOtp());
        }


        return new ResponseEntity<String>("verification otp sent successfully", HttpStatus.OK);
    }

    @PatchMapping("/api/users/enable-two-factor/verify-otp/{otp}")
    public ResponseEntity<User> enableTwoFactorAuthentication(@PathVariable String otp,@RequestHeader("Authorization") String  jwt) throws Exception {
        User user  = userService.findUserProfileByJwt(jwt);

        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());
        String sendTo = verificationCode.getVerificationType().equals(VerificationType.EMAIL)?
                verificationCode.getEmail():verificationCode.getMobile();

        boolean isVerified = verificationCode.getOtp().equals(otp);

        if (isVerified) {
            User updateUser = userService.enableTwoFactorAuthentication(
                    verificationCode.getVerificationType(),sendTo,user);

            verificationCodeService.deleteVerificationCodeById(verificationCode);
            return new ResponseEntity<>(updateUser,HttpStatus.OK);
        }

       throw new Exception("wrong otp");
    }

}
