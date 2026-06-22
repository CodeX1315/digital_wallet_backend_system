package com.example.digitalwallet.wallet.dto;

import java.math.BigDecimal;

public record WalletResponse(
     Long id,
     BigDecimal balance
) {}
