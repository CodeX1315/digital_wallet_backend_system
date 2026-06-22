package com.example.digitalwallet.wallet;

import com.example.digitalwallet.wallet.dto.RegisterRequest;
import com.example.digitalwallet.wallet.dto.RegisterResponse;
import com.example.digitalwallet.wallet.entity.Role;
import com.example.digitalwallet.wallet.entity.User;
import com.example.digitalwallet.wallet.entity.Wallet;
import com.example.digitalwallet.wallet.exception.UserAlreadyExistsException;
import com.example.digitalwallet.wallet.mapper.UserMapper;
import com.example.digitalwallet.wallet.repository.UserRepository;
import com.example.digitalwallet.wallet.repository.WalletRepository;
import com.example.digitalwallet.wallet.service.EmailService;
import com.example.digitalwallet.wallet.service.UserService;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserMapper userMapper;


    @Disabled
    @Test
    void newUserRegisterTest() throws MessagingException, UnsupportedEncodingException {
        RegisterRequest request = new RegisterRequest("rahul@gmail.com", "rahul");
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(anyString()))
                .thenReturn("abcedefg*2$*hijklmn");

        User fakeUser = User.builder()
                .id(1L)
                .email("rahul@gmail.com")
                .role(Role.USER)
                .build();

        when(userMapper.toEntity(any(RegisterRequest.class))).thenReturn(fakeUser);
        when(userRepository.save(any(User.class))).thenReturn(fakeUser);

//        when(walletRepository.save(any(Wallet.class)))
//                .thenReturn(new Wallet());
        RegisterResponse response = RegisterResponse.builder()
                .id(1L)
                .email("rahul@gmail.com")
                .role(Role.USER)
                .build();

        when(userMapper.toResponseDto(any(User.class))).thenReturn(response);
        RegisterResponse registerResponse = userService.registerUser(request);

        assertNotNull(registerResponse);
        assertEquals("rahul@gmail.com", response.email());

        verify(userRepository).findByEmail(anyString());
        verify(passwordEncoder).encode(anyString());
        verify(userRepository).save(any(User.class));
        verify(emailService).sendMail(anyString(), anyString(), anyString());
    }

    @Test
    void registerWithExistingEmailShouldThrownException(){
        RegisterRequest registerRequest = new RegisterRequest("ella@gmail.com", "ella");

        User existingUser = User.builder()
                .email("ella@gmail.com").build();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(existingUser));

        assertThrows(UserAlreadyExistsException.class, ()-> {
            userService.registerUser(registerRequest);
        });

        verify(userRepository, never()).save(any(User.class));
        verify(walletRepository, never()).save(any(Wallet.class));

    }

}
