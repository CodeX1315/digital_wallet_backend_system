package com.example.digitalwallet.wallet.service;

import com.example.digitalwallet.wallet.dto.RegisterRequest;
import com.example.digitalwallet.wallet.dto.RegisterResponse;
import com.example.digitalwallet.wallet.dto.TransactionResponse;
import com.example.digitalwallet.wallet.entity.Transaction;
import com.example.digitalwallet.wallet.entity.User;
import com.example.digitalwallet.wallet.entity.Wallet;
import com.example.digitalwallet.wallet.exception.UserAlreadyExistsException;
import com.example.digitalwallet.wallet.mapper.UserMapper;
import com.example.digitalwallet.wallet.repository.TransactionRepository;
import com.example.digitalwallet.wallet.repository.UserRepository;
import com.example.digitalwallet.wallet.repository.WalletRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private EmailService emailService;

    @Transactional
    public RegisterResponse registerUser(RegisterRequest registerRequest){

        if(userRepository.findByEmail(registerRequest.email()).isPresent()){
            throw new UserAlreadyExistsException("User already exists");
        }


        User user = userMapper.toEntity(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        Wallet wallet = Wallet.builder()
                .balance(new BigDecimal("100.00"))
                .user(user)
                .build();
        user.setWallet(wallet);
        User savedUser = userRepository.save(user);
        emailService.sendMail(
                savedUser.getEmail(),
                "Digital wallet account create successfully",
                "The account is create successfully with this email: "
        );
        return userMapper.toResponseDto(savedUser);
    }

    public User updateUser(RegisterRequest registerRequest) throws UsernameNotFoundException{
        java.lang.String updateUser = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User user = userRepository.findByEmail(updateUser)
                .orElseThrow(() -> new UsernameNotFoundException("User not found in DB"));
        if (registerRequest.email() != null) {
            user.setEmail(registerRequest.email());
        }
        if (registerRequest.password() != null) {
            user.setPassword(passwordEncoder.encode(registerRequest.password()));
        }
        return userRepository.save(user);

    }

    public RegisterResponse getUserById(Long id){
        User user = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
        );
        return userMapper.toResponseDto(user);
    }

    public List<TransactionResponse> getTransactions(){
        String username = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User user = userRepository.findByEmail(username).orElseThrow(
                ()-> new UsernameNotFoundException("User not found"));
        Wallet wallet = walletRepository.findByUser(user).orElseThrow(
                ()-> new RuntimeException("Wallet not found"));
        List<Transaction> transactionList = transactionRepository.findByWalletOrderByCreatedAtDesc(wallet);
        List<TransactionResponse> responses = transactionList.stream()
                .map( transaction -> TransactionResponse.builder()
                        .type(transaction.getType())
                        .amount(transaction.getAmount())
                        .description(transaction.getDescription())
                        .createdAt(transaction.getCreatedAt())
                        .build()).toList();
        return responses;
    }
}
