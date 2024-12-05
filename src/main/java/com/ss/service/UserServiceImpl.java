package com.ss.service;

import com.ss.config.JwtProvider;
import com.ss.domain.VerificationType;
import com.ss.model.TwoFactorAuth;
import com.ss.model.User;
import com.ss.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;


    @Override
    public User findUserProfileByJwt(String jwt) {
        String email = JwtProvider.getEmailFromToken(jwt);
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new RuntimeException("User not found ");
        }
        return user;
    }

    @Override
    public User findUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new RuntimeException("User not found ");
        }
        return user;
    }

    @Override
    public User findUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("user not found");
        }
        return user.get();
    }

    @Override
    public User enableTwoFactorAuthentication(VerificationType verificationType, String sendTo, User user) {
        TwoFactorAuth twoFactorAuth = new TwoFactorAuth();
        twoFactorAuth.setEnable(true);
        twoFactorAuth.setSendTo(verificationType);
        user.setTwoFactorAuth(twoFactorAuth);
        return userRepository.save(user);

    }


    @Override
    public User updatePassword(User user, String newPassword) {
        user.setPassword(newPassword);

        return userRepository.save(user);
    }
}
