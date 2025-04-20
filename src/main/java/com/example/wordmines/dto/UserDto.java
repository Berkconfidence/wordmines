package com.example.wordmines.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UserDto {

    private String username;
    private String email;
    private String password;
    private Float success_rate;
    private Date created;

}
