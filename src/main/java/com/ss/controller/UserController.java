package com.ss.controller;

import com.ss.request.ForgotPasswordTokenRequest;
import com.ss.domain.VerificationType;
import com.ss.model.ForgotPasswordToken;
import com.ss.model.User;
import com.ss.model.VerificationCode;
import com.ss.request.ResetPasswordRequest;
import com.ss.response.ApiResponse;
import com.ss.response.AuthResponse;
import com.ss.service.EmailService;
import com.ss.service.ForgotPasswordService;
import com.ss.service.UserService;
import com.ss.service.VerificationCodeService;
import com.ss.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private ForgotPasswordService forgotPasswordService;


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

    @PostMapping("/auth/users/reset-password/send-otp")
    public ResponseEntity<AuthResponse> sendForgotPasswordOtp(

            @RequestBody ForgotPasswordTokenRequest req) throws Exception {

        User user = userService.findUserByEmail(req.getSendTo());
        String otp = OtpUtils.generateOtp();
        UUID uuid= UUID.randomUUID();
        String id = uuid.toString();

        ForgotPasswordToken token = forgotPasswordService.findByUser(user.getId());
        if (token == null) {
            token = forgotPasswordService.createToken(user, id, otp,  req.getVerificationType(), req.getSendTo()
                    );

        }
        if (req.getVerificationType().equals(VerificationType.EMAIL)) {
            emailService.sendVerificationOTPEmail(
                    user.getEmail(),
                    token.getOtp());
        }

        AuthResponse response=  new AuthResponse();
        response.setSession(token.getId());
        response.setMessage("password reset otp sent successfully");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/auth/users/reset-password/verify-otp")
    public ResponseEntity<ApiResponse> verifyForgotPasswordOtp(
            @RequestParam String id,
            @RequestBody ResetPasswordRequest req,
            @RequestHeader("Authorization") String jwt) throws Exception {
            ForgotPasswordToken forgotPasswordToken = forgotPasswordService.findById(id);
            boolean isVerified = forgotPasswordToken.getOtp().equals(req.getOtp());
            if (isVerified) {
                userService.updatePassword(forgotPasswordToken.getUser(), req.getPassword());
                ApiResponse res = new ApiResponse();
                res.setMessage("password update successfully");
                return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
            }
            throw new Exception("wrong otp");
    }
}
