package com.example.digitalwallet.wallet.dto;

import lombok.Builder;

@Builder
public record RegisterRequest(
        String email,
        String password
) {}
