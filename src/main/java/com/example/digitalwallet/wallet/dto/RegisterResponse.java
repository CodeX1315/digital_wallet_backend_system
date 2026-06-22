package com.example.digitalwallet.wallet.dto;

import com.example.digitalwallet.wallet.entity.Role;
import lombok.Builder;

import java.util.Optional;

@Builder
public record RegisterResponse(
        Long id,
        String email,
        Role role,
        WalletResponse wallet
) {
}
