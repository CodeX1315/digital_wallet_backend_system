package com.example.digitalwallet.wallet.controller;


import com.example.digitalwallet.wallet.dto.*;
import com.example.digitalwallet.wallet.entity.Transaction;
import com.example.digitalwallet.wallet.entity.User;
import com.example.digitalwallet.wallet.repository.UserRepository;
import com.example.digitalwallet.wallet.service.EmailService;
import com.example.digitalwallet.wallet.service.UserService;
import com.example.digitalwallet.wallet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Tag( name = "User Authenticated API", description = "Only authenticate user have access to use this API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WalletService walletService;


    //Email notification
    @Operation(
            summary = "Check balance",
            description = "Fetch user balance from DB"
    )
    @GetMapping("/balance")
    public ResponseEntity<?> checkBalance(){
        BigDecimal balance = walletService.checkBalance();
        return ResponseEntity.ok(new BalanceResponse(balance));
    }

    @Operation(
            summary = "Check transaction details",
            description = "Fetch all transaction related to user"
    )
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions(){
        List<TransactionResponse> transactionList = userService.getTransactions();
        return new ResponseEntity<>(transactionList, HttpStatus.OK);
    }

    @Operation(
            summary = "TopUp the account",
            description = "Add money to your account"
    )
    @PostMapping("/topup")
    public ResponseEntity<?> topUp(@Valid @RequestBody TopUpRequest request) throws MessagingException, UnsupportedEncodingException {
        BigDecimal updatedBalance = walletService.topUp(request);
        return ResponseEntity.ok(new updatedBalance(updatedBalance));
    }

    @Operation(
            summary = "Send money",
            description = "Send money to someone"
    )
    @PostMapping("/send")
    public ResponseEntity<String> sendAmount(@Valid @RequestBody SendMoney sendMoney) throws MessagingException, UnsupportedEncodingException {
        String success = walletService.sendMoney(sendMoney);
        return new ResponseEntity<>(success, HttpStatus.OK);
    }


    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody RegisterRequest registerRequest){
        User updated = userService.updateUser(registerRequest);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(){
        String user = Objects.requireNonNull(Objects.
                requireNonNull(SecurityContextHolder.
                        getContext().getAuthentication()).getName());
        userRepository.deleteByEmail(user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
