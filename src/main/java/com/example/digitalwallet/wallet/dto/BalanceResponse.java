package com.example.digitalwallet.wallet.dto;

import java.math.BigDecimal;

public record BalanceResponse(
        BigDecimal balance
) { }
