package com.example.crud_mysql.service;

import com.example.crud_mysql.model.Enum.LifeCycle;
import com.example.crud_mysql.model.Enum.Role;
import com.example.crud_mysql.model.User;
import com.example.crud_mysql.repository.UserRepository;
import com.example.crud_mysql.request.LogInRequest;
import com.example.crud_mysql.request.RegisterRequest;
import com.example.crud_mysql.response.JwtResponse;
import com.example.crud_mysql.response.MessageResponse;
import com.example.crud_mysql.security.jwt.JwtUtils;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;


    // Constructor injection
    public UserService(UserRepository userRepository, PasswordEncoder encoder,AuthenticationManager authenticationManager,JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils=jwtUtils;
    }

    public MessageResponse registerUser(RegisterRequest signUpRequest) {
        // Check if the username and email already exists
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new MessageResponse("Error: Username is already taken!");
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new MessageResponse("Error: Email is already in use!");
        }
        System.out.println("Registering user: " + signUpRequest.getUsername());

        // Create a new User and populate its fields
        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        Set<Role> roles = signUpRequest.getRole().stream()
                .map(roleName -> Role.valueOf(roleName.name()))  // Convert string to Role enum
                .collect(Collectors.toSet());

        user.setRoles(roles);

        System.out.println("Regist user: " + user.getUsername());

        userRepository.saveAndFlush(user);

        return new MessageResponse("User registered successfully!");
    }


    public JwtResponse authenticateUser(LogInRequest loginRequest) {
        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // Set the authentication context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Get user details
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Get the roles of the user
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Return the JwtResponse with the user's data
        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles);
    }


    // CRUD

    public Page<User> searchUsers(Pageable pageable) {
        return userRepository.findAllByLifeCycle(pageable, LifeCycle.READY);
    }

    public MessageResponse editUser(Long userId, RegisterRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error: User not found"));

        if(request.getUsername() !=null && !request.getUsername().isEmpty()){
            user.setUsername(request.getUsername());
        }

        if(request.getEmail() !=null && !request.getEmail().isEmpty()){
            user.setEmail(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(encoder.encode(request.getPassword()));
        }

        if (request.getRole() != null && !request.getRole().isEmpty()) {
            Set<Role> roles = request.getRole().stream()
                    .map(roleName -> Role.valueOf(roleName.name()))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }
        userRepository.save(user);

        return new MessageResponse("User updated successfully!");
    }

    public boolean deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error: User not found"));

        user.setLifeCycle(LifeCycle.DELETED);
        userRepository.save(user);
        return true;
    }













}