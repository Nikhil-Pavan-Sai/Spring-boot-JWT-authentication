package com.example.springbootcustomjwtauth.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ApiResponse {
    private Boolean success;
    private String message;
}