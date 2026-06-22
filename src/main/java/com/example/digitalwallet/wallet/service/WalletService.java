package com.example.digitalwallet.wallet.service;

import com.example.digitalwallet.wallet.dto.SendMoney;
import com.example.digitalwallet.wallet.dto.TopUpRequest;
import com.example.digitalwallet.wallet.dto.WalletResponse;
import com.example.digitalwallet.wallet.entity.Transaction;
import com.example.digitalwallet.wallet.entity.TransactionType;
import com.example.digitalwallet.wallet.entity.User;
import com.example.digitalwallet.wallet.entity.Wallet;
import com.example.digitalwallet.wallet.repository.TransactionRepository;
import com.example.digitalwallet.wallet.repository.UserRepository;
import com.example.digitalwallet.wallet.repository.WalletRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class WalletService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private EmailService emailService;

    public BigDecimal checkBalance(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assert auth != null;
        String username = auth.getName();
        User user = userRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found"));
        return user.getWallet().getBalance();
    }

    @Transactional
    public  BigDecimal topUp(TopUpRequest request) throws MessagingException, UnsupportedEncodingException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assert auth != null;
        String username = auth.getName();
        User user = userRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found"));
        Wallet wallet = walletRepository.findByUser(user).orElseThrow(
                () -> new UsernameNotFoundException("Wallet not found"));

        wallet.setBalance(wallet.getBalance().add(request.getAmount()));

        walletRepository.save(wallet);
        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.TOP_UP)
                .amount(request.getAmount())
                .description("TopUp account")
                .build();
        transactionRepository.save(transaction);

        emailService.sendMail(
                username,
                "Account topUp successfully",
                "You successfully add money in your account" +
                        "\nAmount: ₹"+request.getAmount()+
                        "\nYour updated balance is: "+wallet.getBalance()
        );

        return wallet.getBalance();
    }

    @Transactional
    public String sendMoney(SendMoney sendMoney) throws MessagingException, UnsupportedEncodingException {
        //1st we get user by using SecurityContextHolder
        java.lang.String username = Objects.requireNonNull(
                SecurityContextHolder.getContext().getAuthentication()).getName();
        //2nd we get both user's from DB
        User sender = userRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("Sender not found"));
        User receiver = userRepository.findByEmail(sendMoney.getReceiversEmail()).orElseThrow(
                ()-> new UsernameNotFoundException("Receiver not found"));
        //3rd check validation whether the sender is not sending money to themselves
        if (sender.getEmail().equals(receiver.getEmail())){
            throw new RuntimeException("You can't transfer money to yourself");
        }

        //4th get both sender and receiver wallet
        Wallet senderWallet = walletRepository.findByUser(sender).orElseThrow(
                ()-> new RuntimeException("Wallet not found of sender"));
        Wallet receiverWallet = walletRepository.findByUser(receiver).orElseThrow(
                () -> new RuntimeException("Wallet not found of receiver"));
        //5 check sender balance is not less than sending amount
        if (senderWallet.getBalance().compareTo(sendMoney.getAmount()) < 0){
            throw new RuntimeException("Insufficient balance");
        }

        //6 reducing amount from senders account
        senderWallet.setBalance(senderWallet.getBalance().subtract(sendMoney.getAmount()));
        //7 adding money in receivers account
        receiverWallet.setBalance(receiverWallet.getBalance().add(sendMoney.getAmount()));

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        //Transaction history about sending money to whom's
        Transaction debit = Transaction.builder()
                .wallet(senderWallet)
                .type(TransactionType.TRANSFER_OUT)
                .amount(sendMoney.getAmount())
                .description("Transfer to: "+receiver.getEmail())
                .build();
        transactionRepository.save(debit);

        //If sending then someone receiving so we have to save transaction from who we're receiving money
        Transaction credit = Transaction.builder()
                .wallet(receiverWallet)
                .type(TransactionType.TRANSFER_IN)
                .amount(sendMoney.getAmount())
                .description("Received from: "+sender.getEmail())
                .build();
        transactionRepository.save(credit);

        emailService.sendMail(
                sender.getEmail(),
                "Successfully transfer money",
                "You successfully transfer ₹"+sendMoney.getAmount()+
                        " to "+receiver.getEmail()+
                        "\nYour updated balance is: "+senderWallet.getBalance()
        );

        emailService.sendMail(
                receiver.getEmail(),
                "Received money in account",
                "You have received ₹"+sendMoney.getAmount()+
                        " from "+sender.getEmail()+
                        "\nYour updated balance is: "+receiverWallet.getBalance()
        );

        return "Transaction successful in this account: "+receiver.getEmail();
    }

}
