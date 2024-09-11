package com.example.userservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class LoginResponseDTO {
    private String tokenValue;
    private String email;
    private Date expiryAt;
    private ResponseStatus responseStatus;
}
