package com.ss.controller;

import com.ss.config.JwtProvider;
import com.ss.model.TwoFactorOTP;
import com.ss.repository.UserRepository;
import com.ss.response.AuthResponse;
import com.ss.service.CustomUserDetailsService;
import com.ss.service.EmailService;
import com.ss.service.TwoFactorOtpService;
import com.ss.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.ss.model.User;
@RestController
@RequestMapping("/auth")
public class AuthController {
    // Implement authentication logic here
   @Autowired
   private UserRepository userRepository;

   @Autowired
   private CustomUserDetailsService customUserDetailsService;

   @Autowired
   private TwoFactorOtpService twoFactorOtpService;

   @Autowired
   private OtpUtils otpUtils;

   @Autowired
   private EmailService emailService;

   @PostMapping("/signup")
   public ResponseEntity<AuthResponse> registerUser(@RequestBody User user) throws Exception{

       User isEmailExist = userRepository.findByEmail(user.getEmail());
       if(isEmailExist!=null){
           throw  new Exception("email is already used with another account");
       }

       //if email is not exist then create a new user
       //In first step we create a new user
       // In second we get the email from the user and set in the newUser property
       User newUser = new User();
       newUser.setFullName(user.getFullName());
       newUser.setPassword(user.getPassword());
       newUser.setEmail(user.getEmail());

       Authentication auth = new UsernamePasswordAuthenticationToken(
               user.getEmail(),
               user.getPassword()
       );

       SecurityContextHolder.getContext().setAuthentication(auth);

       //Now, we will generate a token and return it to the client
       String jwt = JwtProvider.generateToken(auth);



       //this line will save the user details in database
       User savedUser = userRepository.save(newUser);

       AuthResponse response= new AuthResponse();
       response.setJwt(jwt);
       response.setStatus(true);
       response.setMessage("register success");

       return new ResponseEntity<>(response, HttpStatus.CREATED);

   }

   // Implement login logic here
    @PostMapping("/signin")  // POST request for login
   public ResponseEntity<AuthResponse> login(@RequestBody User user) throws Exception{

    String username = user.getEmail();
    String password = user.getPassword();


       Authentication auth = authenticate(username, password);

       SecurityContextHolder.getContext().setAuthentication(auth);

       //Now, we will generate a token and return it to the client
       String jwt = JwtProvider.generateToken(auth);

        User authUser = userRepository.findByEmail(username);

        //check if user has enabled two-factor authentication or not
        // if enabled, generate a two-factor authentication code and send it to the user's registered email or phone number
        // then, the user should input this code to verify their identity and access the system
        // if the code is correct, then allow the user to login to the system, else, return an error message
        // In this example, we will just print a message for demonstration purpose
        // In a real-world application, you should implement the logic to verify the code and allow the user to login or return an error message if the code is invalid
        if(user.getTwoFactorAuth().isEnable()){
            AuthResponse res = new AuthResponse();
            res.setMessage("Two factor auth is enabled");
            res.setTwoFactorAuthEnabled(true);
            String otp = OtpUtils.generateOtp();

            TwoFactorOTP oldTwoFactorOPT = twoFactorOtpService.findByUser(authUser.getId());
            if(oldTwoFactorOPT != null){
                twoFactorOtpService.deleteTwoFactorOtp(oldTwoFactorOPT);
            }
            TwoFactorOTP newTwoFactorOtp = twoFactorOtpService.createTwoFactorOtp(
                    authUser,otp, jwt);

            emailService.sendVerificationOTPEmail(username, otp);

            res.setSession(newTwoFactorOtp.getId());
            return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
        }


       //this line will save the user details in database
//       User savedUser = userRepository.save(newUser);

       AuthResponse response= new AuthResponse();
       response.setJwt(jwt);
       response.setStatus(true);
       response.setMessage("login success");

       return new ResponseEntity<>(response, HttpStatus.CREATED);

   }

    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        if (userDetails == null) {
            throw new BadCredentialsException("invalid username ");
        }
        if (!password.equals(userDetails.getPassword()) ) {
            throw new BadCredentialsException("invalid password");
        }
       return new UsernamePasswordAuthenticationToken(userDetails,password, userDetails.getAuthorities());
    }

    @PostMapping("/two-factor/otp/{otp}")
    public ResponseEntity<AuthResponse> verifySignInOtp(
            @PathVariable String otp,
            @RequestParam String id) throws Exception {

       TwoFactorOTP twoFactorOTP = twoFactorOtpService.findById(id);
       if(twoFactorOtpService.verifyTwoFactorOtp(twoFactorOTP,otp)){
           AuthResponse response = new AuthResponse();
           response.setMessage("Two factor authentication is verified");
           response.setTwoFactorAuthEnabled(true);
           response.setJwt(twoFactorOTP.getJwt());
           return new ResponseEntity<>(response, HttpStatus.OK);
       }
       throw new Exception("Invalid otp");

    }
}
