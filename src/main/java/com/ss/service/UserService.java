package com.ss.service;

import com.ss.model.User;

public interface UserService {
    public User findUserProfileByJwt(String jwt);
    public User findUserByEmail(String email);
    public User findUserById(String id);

    public User enableTwoFactorAuthentication(User user);

    public User updatePassword(User user, String newPassword);


}
