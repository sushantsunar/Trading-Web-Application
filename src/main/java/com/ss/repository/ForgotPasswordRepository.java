package com.ss.repository;

import com.ss.model.ForgotPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPasswordToken, String> {

    ForgotPasswordToken findByUserId(Long userId);

}
