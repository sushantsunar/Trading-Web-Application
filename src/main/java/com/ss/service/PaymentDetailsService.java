package com.ss.service;

import com.ss.model.PaymentDetails;
import com.ss.model.User;

public interface PaymentDetailsService {
public PaymentDetails addPaymentDetails(String accountNumber,
                                        String accountHolderName,
                                        String ifsc,
                                        String bankName,
                                        User user);

    public PaymentDetails getPaymentDetails(User user);

}
