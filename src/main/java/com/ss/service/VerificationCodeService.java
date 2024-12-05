package com.ss.service;

import com.ss.domain.VerificationType;
import com.ss.model.User;
import com.ss.model.VerificationCode;

public interface VerificationCodeService {
    VerificationCode sendVerificationCode(User user , VerificationType verificationType);

    VerificationCode getVerificationCodeById(Long id) throws Exception;
    VerificationCode getVerificationCodeByUser(Long userId);



    void deleteVerificationCodeById(VerificationCode verificationCode);


}
