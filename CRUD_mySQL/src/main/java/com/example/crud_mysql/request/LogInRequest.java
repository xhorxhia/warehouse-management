package com.example.crud_mysql.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class LogInRequest {

    @NotNull
    private String username;

    @NotNull
    private String password;


}
