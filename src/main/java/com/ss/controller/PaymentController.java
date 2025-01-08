package com.ss.controller;

import com.razorpay.RazorpayException;
import com.ss.domain.PaymentMethod;
import com.ss.model.PaymentOrder;
import com.ss.model.User;
import com.ss.response.PaymentResponse;
import com.ss.service.PaymentService;
import com.ss.service.UserService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PaymentController {
     @Autowired
     private PaymentService paymentService;
    @Autowired
    private UserService userService;

    @PostMapping("/api/payment/{payment Method}/amount/{amount}")
    public ResponseEntity<PaymentResponse> paymentHandler(
            @PathVariable PaymentMethod paymentMethod,
            @PathVariable Long amount,
            @RequestHeader("Authorization") String jwt) throws
            Exception,
            RazorpayException,
            StripeException {
        User user = userService.findUserProfileByJwt(jwt);
        PaymentResponse paymentResponse;

        PaymentOrder order = paymentService.createOrder(user, amount, paymentMethod);

        if (paymentMethod.equals (PaymentMethod. RAZORPAY)){
            paymentResponse=paymentService.createRazorpayPaymentLink (user,amount);
        }else{
            paymentResponse=paymentService.createStripePaymentLink (user,amount,order.getId());
        }
        return new ResponseEntity<>(paymentResponse, HttpStatus.CREATED);
    }

}
