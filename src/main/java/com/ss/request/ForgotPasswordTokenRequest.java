package com.ss.request;

import com.ss.domain.VerificationType;
import lombok.Data;

@Data
public class ForgotPasswordTokenRequest {
    private String sendTo;
    private VerificationType verificationType;
}
