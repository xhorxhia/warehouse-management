package com.example.crud_mysql.controller;

import com.example.crud_mysql.model.User;
import com.example.crud_mysql.request.LogInRequest;
import com.example.crud_mysql.request.RegisterRequest;
import com.example.crud_mysql.response.JwtResponse;
import com.example.crud_mysql.response.MessageResponse;;

import com.example.crud_mysql.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {

   private final UserService userService;

    // Constructor injection
    public AuthController(UserService userService) {
         this.userService = userService;
    }


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LogInRequest loginRequest) {
        JwtResponse jwtResponse = this.userService.authenticateUser(loginRequest);
        System.out.println(jwtResponse);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest signUpRequest) {
        MessageResponse messageResponse = this.userService.registerUser(signUpRequest);
        if(messageResponse.getMessage().equalsIgnoreCase("User registered successfully!")){
            return ResponseEntity.ok(messageResponse);
        }

        return ResponseEntity.badRequest().body(messageResponse);
    }
}
