package com.ss.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ss.domain.USER_ROLE;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String fullName;
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Embedded
    private TwoFactorAuth twoFactorAuth = new TwoFactorAuth();

//    IT IS A DEFAULT ROLE WHICH IS CUSTOMER
    private USER_ROLE role = USER_ROLE.ROLE_CUSTOMER;
}