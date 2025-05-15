package com.example.crud_mysql.controller;

import com.example.crud_mysql.model.User;
import com.example.crud_mysql.request.RegisterRequest;
import com.example.crud_mysql.response.MessageResponse;
import com.example.crud_mysql.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/search")
    public Page<User> findAll(Pageable pageable){
        return userService.searchUsers(pageable);
    }

    @PostMapping("create-user")
    public ResponseEntity<?> createUser(@RequestBody RegisterRequest registerRequest){
        MessageResponse response = userService.registerUser(registerRequest);

        if(response.getMessage().equalsIgnoreCase("User registered successfully")){
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @PutMapping("/{id}/edit")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long id, @RequestBody RegisterRequest registerRequest){
        MessageResponse response = userService.editUser(id, registerRequest);
        if(response.getMessage().equalsIgnoreCase("User updated successfully")){
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @DeleteMapping("/{id}/soft-delete")
    public ResponseEntity<?> softDeleteUser(@PathVariable("id") Long id){
        boolean result = userService.deleteUser(id);
        return ResponseEntity.ok(result);
    }
}
