package com.example.digitalwallet.wallet.controller;


import com.example.digitalwallet.wallet.dto.RegisterRequest;
import com.example.digitalwallet.wallet.dto.RegisterResponse;
import com.example.digitalwallet.wallet.dto.TransactionResponse;
import com.example.digitalwallet.wallet.dto.WalletResponse;
import com.example.digitalwallet.wallet.entity.Transaction;
import com.example.digitalwallet.wallet.entity.User;
import com.example.digitalwallet.wallet.repository.TransactionRepository;
import com.example.digitalwallet.wallet.repository.UserRepository;
import com.example.digitalwallet.wallet.service.TransactionService;
import com.example.digitalwallet.wallet.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "This is Admin only API", description = "Only admin have access to this API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionService transactionService;

    @Operation(
            summary = "Get all users data",
            description = "Fetch all users data with response DTO so no data exposing"

    )
    @GetMapping("/users")
    public ResponseEntity<List<RegisterResponse>> getAllUsers(){
        List<User> userList = new ArrayList<>();
        userList = userRepository.findAll();
        List<RegisterResponse> responses = userList.stream()
                .map( user -> RegisterResponse.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .wallet( new WalletResponse(
                                user.getWallet().getId(),
                                user.getWallet().getBalance())).build()
                ).toList();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }


    @Operation(
            summary = "Get all transactions",
            description = "Fetch all transactions with response DTO so no data expose"
    )
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions(){
        List<TransactionResponse> transactions =  transactionService.getAllTransaction();
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @Operation(
            summary = "Get user by ID",
            description = "Fetch user from database using ID"
    )
    @GetMapping("/user/{id}")
    public ResponseEntity<RegisterResponse> getUserById(@PathVariable Long id){
        RegisterResponse getUser = userService.getUserById(id);
        return new ResponseEntity<>(getUser, HttpStatus.OK);
    }

    //admin can have access to see particular users transaction history
    @Operation(
            summary = "Get transaction by ID",
            description = "Fetch transaction of particular user"
    )
    @GetMapping("/user/transactions/{id}")
    public ResponseEntity<List<TransactionResponse>> getTransactionHistoryById(@PathVariable Long id){
        List<TransactionResponse> transactions = transactionService.getTransactionOfUserById(id);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    //Use response DTO to get all user's in admin controll but using stream check claude
}
