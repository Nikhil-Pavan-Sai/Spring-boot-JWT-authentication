package com.example.springbootcustomjwtauth.controller;

import com.example.springbootcustomjwtauth.model.RoleType;
import com.example.springbootcustomjwtauth.model.User;
import com.example.springbootcustomjwtauth.payload.ApiResponse;
import com.example.springbootcustomjwtauth.payload.LoginRequest;
import com.example.springbootcustomjwtauth.payload.SignUpRequest;
import com.example.springbootcustomjwtauth.repository.UserRepository;
import com.example.springbootcustomjwtauth.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;

    @PostMapping("/validate")
    private String validateUser(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return tokenProvider.generateToken(authentication);
    }

    @PostMapping("/signIn")
    public Map<String, String> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        String token = validateUser(loginRequest);
        Map<String, String> userDetails = new HashMap<>();
        if(userRepository.findById(tokenProvider.getUserIdFromJWT(token)).isPresent()) {
            userDetails.put("status", "Authentication Successful");
            userDetails.put("email", userRepository.findById(tokenProvider.getUserIdFromJWT(token)).get().getEmail());
            userDetails.put("username", userRepository.findById(tokenProvider.getUserIdFromJWT(token)).get().getUsername());
            userDetails.put("role", userRepository.findById(tokenProvider.getUserIdFromJWT(token)).get().getRoleType().toString());
        }
        return userDetails;
    }

    @PostMapping("/signUp")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if(userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity(new ApiResponse(false, "Username is already taken!"),
                    HttpStatus.BAD_REQUEST);
        }

        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity(new ApiResponse(false, "Email Address already in use!"),
                    HttpStatus.BAD_REQUEST);
        }
        // Creating user's account
        Set<RoleType> roleTypes = new HashSet<>();
        roleTypes.add(signUpRequest.getRoleType());
        User user = new User(signUpRequest.getName(), signUpRequest.getUsername(),
                signUpRequest.getEmail(), signUpRequest.getPassword(), roleTypes);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User result = userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(result.getUsername()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }

    @GetMapping("/allUsers")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/allUsers/{usernameOrEmail}")
    public Optional<User> getSingleUser(@Valid @PathVariable String usernameOrEmail) {
        return userRepository.findByUsername(usernameOrEmail);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health(){
        return new ResponseEntity<String>("alive",HttpStatus.OK);
    }
}