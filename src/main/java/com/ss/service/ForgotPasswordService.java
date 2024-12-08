package com.ss.service;

import com.ss.domain.VerificationType;
import com.ss.model.ForgotPasswordToken;
import com.ss.model.User;

public interface ForgotPasswordService {
    ForgotPasswordToken createToken(User user,
                                    String id, String otp,
                                    VerificationType verificationType,
                                    String sendTo);

    ForgotPasswordToken findById(String id);

    ForgotPasswordToken findByUser(Long userId);

    void deleteToken(ForgotPasswordToken token);
}
