package com.example.digitalwallet.wallet.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Async
    public void sendMail(String sentTo, String subject, String body) throws MessagingException, UnsupportedEncodingException {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper mime = new MimeMessageHelper(message, true);
            mime.setTo(sentTo);
            mime.setSubject(subject);
            mime.setText(body);
            mime.setFrom("noreply@digitalwallet.com", "Digital Wallet");
            javaMailSender.send(message);
        } catch (Exception e) {
            System.out.println("EMAIL FAILED: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
        }
    }
}
