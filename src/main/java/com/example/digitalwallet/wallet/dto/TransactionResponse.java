package com.example.digitalwallet.wallet.dto;

import com.example.digitalwallet.wallet.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
    private TransactionType type;
    private BigDecimal amount;
    private String description;
    private Date createdAt;
}