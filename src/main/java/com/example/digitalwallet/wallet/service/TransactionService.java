package com.example.digitalwallet.wallet.service;

import com.example.digitalwallet.wallet.dto.TransactionResponse;
import com.example.digitalwallet.wallet.entity.Transaction;
import com.example.digitalwallet.wallet.entity.User;
import com.example.digitalwallet.wallet.entity.Wallet;
import com.example.digitalwallet.wallet.repository.TransactionRepository;
import com.example.digitalwallet.wallet.repository.UserRepository;
import com.example.digitalwallet.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WalletRepository walletRepository;

    public List<TransactionResponse> getAllTransaction(){
        List<Transaction> transactionList = transactionRepository.findAll();
        List<TransactionResponse> response = transactionList.stream()
                .map( transaction -> TransactionResponse.builder()
                        .type(transaction.getType())
                        .amount(transaction.getAmount())
                        .description(transaction.getDescription())
                        .createdAt(transaction.getCreatedAt())
                        .build()).toList();
        return response;
    }

    public List<TransactionResponse> getTransactionOfUserById(Long id){
        User user = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User not found"));
        Wallet wallet = walletRepository.findByUser(user).orElseThrow( () ->
                new RuntimeException("Wallet not found"));
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
