package com.example.digitalwallet.wallet.mapper;


import com.example.digitalwallet.wallet.dto.RegisterRequest;
import com.example.digitalwallet.wallet.dto.RegisterResponse;
import com.example.digitalwallet.wallet.dto.WalletResponse;
import com.example.digitalwallet.wallet.entity.User;
import com.example.digitalwallet.wallet.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    RegisterResponse toResponseDto(User user);
    WalletResponse toWalletResponseDto(Wallet wallet);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "wallet", ignore = true )
    @Mapping(target = "role", constant = "USER")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toEntity(RegisterRequest registerRequest);
}
