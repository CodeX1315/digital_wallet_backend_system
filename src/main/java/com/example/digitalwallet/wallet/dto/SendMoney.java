package com.example.digitalwallet.wallet.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMoney {
    @NotNull(message = "Enter email to proceed")
    private String receiversEmail;
    @NotNull
    @DecimalMin(value = "1.00", message = "Minimum sending amount is Rs.1")
    private BigDecimal amount;
}
