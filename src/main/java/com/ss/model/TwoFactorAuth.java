package com.ss.model;

import com.ss.domain.VerificationType;
import lombok.Data;

@Data
public class TwoFactorAuth {
    private boolean isEnable = false;
    private VerificationType sendTo;
}
