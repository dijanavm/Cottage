package com.isa.cottages.Model;

import com.isa.cottages.FieldMatch.FieldMatch;
import lombok.*;

import javax.persistence.Column;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldMatch(first = "password", second = "confirmPassword", message = "The password fields must match!")
public class UserRequest {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String confirmPassword;

    private String residence;

    private String city;

    private String state;

    private String phoneNumber;

    private RegistrationType registrationType;

    private Boolean enabled = false;

    private UserRole userRole;

    private String explanationOfRegistration;
}