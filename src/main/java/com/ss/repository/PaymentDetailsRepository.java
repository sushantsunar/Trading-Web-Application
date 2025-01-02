package com.ss.repository;

import com.ss.model.PaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentDetailsRepository extends JpaRepository<PaymentDetails,Long> {
PaymentDetails findByUserId(Long userId);

}
