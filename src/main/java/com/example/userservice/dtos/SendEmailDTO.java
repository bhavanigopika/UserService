package com.example.userservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class SendEmailDTO {
    private String from;
    private String to;
    private String subject;
    private String body;
}
