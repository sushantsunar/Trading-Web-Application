package com.ss.utils;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service

public class OtpUtils {

    public static String generateOtp() {
        int otpLength = 6;
        Random random = new Random();

        StringBuilder otp = new StringBuilder(otpLength);

        for (int i = 0; i < otpLength; i++) {
            otp.append(random.nextInt(10));
        }

        return otp.toString();
        // Generate a random 6-digit OTP
//        return String.format("%06d", (int) (Math.random() * 1000000));
    }
}
