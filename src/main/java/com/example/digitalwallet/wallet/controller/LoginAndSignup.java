package com.example.digitalwallet.wallet.controller;

import com.example.digitalwallet.wallet.dto.RegisterRequest;
import com.example.digitalwallet.wallet.dto.RegisterResponse;
import com.example.digitalwallet.wallet.repository.UserRepository;
import com.example.digitalwallet.wallet.service.UserService;
import com.example.digitalwallet.wallet.utils.JwtUtility;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;


@Tag(name = "Public API", description = "Public API for Login and Signup")
@RestController
@RequestMapping("/auth")
@Slf4j
public class LoginAndSignup {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtility jwtUtility;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Operation(
            summary = "Signup endpoint",
            description = "Register new user in DB"
    )
    @PostMapping("/signup")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody RegisterRequest registerRequest) throws MessagingException, UnsupportedEncodingException {
        RegisterResponse saveUser= userService.registerUser(registerRequest);
        return new ResponseEntity<>(saveUser, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Login endpoint",
            description = "Login user and return JWT bearer token"
    )
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody RegisterRequest registerRequest){
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(registerRequest.email(), registerRequest.password()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(registerRequest.email());
            String jwt = jwtUtility.generateToken(userDetails.getUsername());
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occur while creatingAuthenticationToken", e);
            return new ResponseEntity<>("Username or password wrong", HttpStatus.BAD_REQUEST);
        }
    }


}
